package me.xemor.enchantedbosses.spawning;

import me.xemor.enchantedbosses.BossHandler;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class SpawnHandler implements Listener {

    private final BossHandler bossHandler;
    private final Random random = new Random();
    private final List<LocalDateTime> localTimes = new ArrayList<>();
    private Optional<LocalDateTime> optionalNextTime;
    private final ReentrantLock timeLock = new ReentrantLock();
    private BukkitTask currentTask;
    private int weightingSum;
    private List<SkillEntity> bosses;

    public SpawnHandler(BossHandler bossHandler) {
        this.bossHandler = bossHandler;
        reload();
        waitForNextTime();
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }
        List<SkillEntity> validSkillEntities = bosses.stream()
                .filter(skillEntity -> skillEntity.getAutoSpawnData() != null)
                .filter((skillEntity) -> skillEntity.getAutoSpawnData().getMode() == SpawnData.Mode.REPLACE)
                .filter((skillEntity) -> skillEntity.getAutoSpawnData().getReplace().inSet(e.getEntityType()))
                .filter((skillEntity) -> checkSpawnConditions(e.getLocation().getBlock(), skillEntity))
                .toList();
        double weightSum = validSkillEntities.stream().map(skillEntity -> skillEntity.getAutoSpawnData().getWeighting()).reduce(Double::sum).orElse(0.0);
        double defaultEntityWeight = bossHandler.getConfigHandler().getDefaultEntityWeight();
        double rng = ThreadLocalRandom.current().nextDouble(weightSum + defaultEntityWeight);
        if (rng > defaultEntityWeight) {
            rng -= defaultEntityWeight;
            double thresholdSum = 0;
            for (SkillEntity entity : validSkillEntities) {
                double currentWeight = entity.getAutoSpawnData().getWeighting();
                thresholdSum += currentWeight;
                if (rng < thresholdSum) {
                    bossHandler.spawn(entity, e.getLocation());
                    e.setCancelled(true);
                }
            }
        }
    }

    public void waitForNextTime() {
        new BukkitRunnable() {
            @Override
            public void run() {
                timeLock.lock();
                try {
                    if (optionalNextTime.isEmpty()) {
                        return;
                    }
                } finally {
                    timeLock.unlock();
                }
                LocalDateTime nextTime = optionalNextTime.get();
                if (currentTask != null) {
                    currentTask.cancel();
                }
                currentTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        timeLock.lock();
                        try {
                            if (LocalDateTime.now().isAfter(nextTime)) {
                                localTimes.remove(nextTime);
                                localTimes.add(LocalDateTime.of(nextTime.plusDays(1).toLocalDate(), nextTime.toLocalTime()));
                                optionalNextTime = calculateNextTime();
                                cancel();
                                waitForNextTime();
                                Bukkit.getScheduler().runTask(EnchantedBosses.getInstance(), SpawnHandler.this::spawnRandomBoss);
                            }
                        } finally {
                            timeLock.unlock();
                        }
                    }
                }.runTaskTimer(EnchantedBosses.getInstance(), 100L, 100L);
            }
        }.runTaskAsynchronously(EnchantedBosses.getInstance());
    }

    public void disable() {
        currentTask.cancel();
    }

    public void reload() {
        weightingSum = 0;
        for (SkillEntity skillEntity : bossHandler.getBosses()) {
            if (skillEntity.getAutoSpawnData() != null && skillEntity.getAutoSpawnData().getMode() == SpawnData.Mode.BOSS) {
                weightingSum += skillEntity.getAutoSpawnData().getWeighting();
            }
        }
        bosses = new ArrayList<>(bossHandler.getBosses());
        timeLock.lock();
        try {
            List<LocalTime> times = bossHandler.getConfigHandler().getBossSpawnTimes();
            localTimes.clear();
            for (LocalTime time : times) {
                if (time.isBefore(LocalTime.now())) {
                    localTimes.add(LocalDateTime.of(LocalDate.now().plusDays(1), time));
                }
                else {
                    localTimes.add(LocalDateTime.of(LocalDate.now(), time));
                }
            }
            optionalNextTime = calculateNextTime();
            if (currentTask != null) {
                currentTask.cancel();
            }
            waitForNextTime();
        } finally {
            timeLock.unlock();
        }
    }

    private Optional<LocalDateTime> calculateNextTime() {
        return localTimes.stream().filter((it) -> it.isAfter(LocalDateTime.now())).sorted().findFirst();
    }

    public Optional<LocalDateTime> getOptionalNextTime() {
        timeLock.lock();
        try {
            return optionalNextTime;
        } finally {
            timeLock.unlock();
        }
    }

    public boolean spawnRandomBoss() {
        int iterations = 0;
        SkillEntity boss;
        do {
            iterations++;
            boss = chooseRandomBoss();
        } while (spawnBoss(boss).isEmpty() && iterations < 25);
        if (iterations == 25) return false;
        return true;
    }

    public Optional<Entity> spawnBoss(SkillEntity boss) {
        List<String> worlds = boss.getAutoSpawnData().getWorlds();
        String worldStr = worlds.get(random.nextInt(worlds.size()));
        World world = Bukkit.getWorld(worldStr);
        if (world == null) {
            Bukkit.getLogger().severe("Invalid world specified for " + boss.getName() + "world: " + worldStr);
            return Optional.empty();
        }
        Chunk[] chunks = world.getLoadedChunks();
        return spawnBoss(boss, chunks);
    }

    public Optional<Entity> spawnBoss(SkillEntity boss, Chunk[] chunks) {
        return spawnBoss(boss, chunks, true);
    }

    public Optional<Entity> spawnBoss(SkillEntity boss, Chunk[] chunks, boolean broadcast) {
        int counter = 0;
        int counterMax = Math.min(100, chunks.length * 2);
        while (counter < counterMax) {
            counter++;
            int rng = random.nextInt(chunks.length);
            Chunk chunk = chunks[rng];
            World world = chunk.getWorld();
            int x = getRandomCoordinate(chunk.getX());
            int z = getRandomCoordinate(chunk.getZ());
            Block toSpawnOn;
            if (boss.getAutoSpawnData().getMaxHeight() == -1 || boss.getAutoSpawnData().getMaxHeight() == world.getMaxHeight()) {
                toSpawnOn = world.getHighestBlockAt(x, z);
                toSpawnOn = toSpawnOn.getRelative(BlockFace.UP);
                if (!checkSpawnConditions(toSpawnOn, boss)) continue;
            } else {
                Location location = new Location(world, x, boss.getAutoSpawnData().getMaxHeight(), z);
                toSpawnOn = findHighestMatchingBlock(location, boss);
                if (toSpawnOn == null) continue;
            }
            Location location = toSpawnOn.getLocation();
            if (broadcast) {
                Component component = MiniMessage.miniMessage().deserialize(bossHandler.getConfigHandler().getBossSpawnMessage(),
                        Placeholder.component("name", boss.getColouredName()),
                        Placeholder.unparsed("x",String.valueOf(location.getBlockX())),
                        Placeholder.unparsed("y",String.valueOf(location.getBlockY())),
                        Placeholder.unparsed("z",String.valueOf(location.getBlockZ())),
                        Placeholder.unparsed("world",world.getName()));
                EnchantedBosses.getBukkitAudiences().all().sendMessage(component);
            }
            return Optional.ofNullable(bossHandler.spawn(boss, location));
        }
        return Optional.empty();
    }

    private boolean checkSpawnConditions(Block block, SkillEntity boss) {
        SpawnData spawnData = boss.getAutoSpawnData();
        boolean correctBiome = spawnData.matchesBiome(block.getBiome());
        boolean correctWorld = boss.getAutoSpawnData().getWorlds().contains(block.getWorld().getName()) || boss.getAutoSpawnData().getWorlds().isEmpty();
        boolean hasSpace = checkSpaceNeeded(block, spawnData.getSpaceNeeded());
        boolean inWorldBorder = block.getWorld().getWorldBorder().isInside(block.getLocation());
        return hasSpace && correctWorld && correctBiome && inWorldBorder;
    }

    private int getRandomCoordinate(int chunkCoordinate) {
        int dx = random.nextInt(16);
        return (chunkCoordinate << 4) + dx;
    }

    public Optional<Entity> spawnBoss(SkillEntity boss, Chunk centreChunk, int radius) {
        return spawnBoss(boss, getChunkSquare(centreChunk, radius));
    }

    public Chunk[] getChunkSquare(Chunk centreChunk, int radius) {
        World world = centreChunk.getWorld();
        int x = centreChunk.getX();
        int z = centreChunk.getZ();
        int diameter = (radius * 2) + 1;
        Chunk[] chunks = new Chunk[diameter * diameter];
        int currentIndex = 0;
        for (int xOffset = -radius; xOffset <= radius; xOffset++) {
            for (int zOffset = -radius; zOffset <= radius; zOffset++) {
                chunks[currentIndex] = world.getChunkAt(x + xOffset, z + zOffset);
                currentIndex++;
            }
        }
        return chunks;
    }

    private SkillEntity chooseRandomBoss() {
        int rng = random.nextInt(weightingSum);
        int currentWeighting = 0;
        for (SkillEntity skillEntity : bosses) {
            SpawnData spawnData = skillEntity.getAutoSpawnData();
            if (spawnData != null && spawnData.getMode() == SpawnData.Mode.BOSS) {
                currentWeighting += spawnData.getWeighting();
                if (currentWeighting >= rng) {
                    return skillEntity;
                }
            }
        }
        return null;
    }

    public Block findHighestMatchingBlock(Location startLocation, SkillEntity boss) {
        World world = startLocation.getWorld();
        Block currentBlock = world.getBlockAt(startLocation);
        while (!(checkSpawnConditions(currentBlock, boss) && (!currentBlock.getType().isAir() || boss.getAutoSpawnData().shouldSpawnOnAir()))) {
            currentBlock = currentBlock.getRelative(0, -1, 0);
            if (currentBlock.getY() <= world.getMinHeight()) {
                return null;
            }

        }
        return currentBlock;
    }

    private boolean checkSpaceNeeded(Block initialBlock, int space) {
        if (space == 0) return true;
        if (initialBlock.getY() == initialBlock.getWorld().getMinHeight()) return false;
        final EnumSet<BlockFace> facesToCheck = EnumSet.of(BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH);
        boolean isSafe = true;
        for (BlockFace face : facesToCheck) {
            boolean isFaceSafe = checkSpaceNeededDirection(initialBlock, face, space);
            if (!isFaceSafe) {
                isSafe = false;
                break;
            }
        }
        return isSafe;
    }

    private boolean checkSpaceNeededDirection(Block initialBlock, BlockFace direction, int space) {
        return checkSpaceNeededDirection(initialBlock, direction, space, 0);
    }

    private boolean checkSpaceNeededDirection(Block block, BlockFace direction, int space, int spaceChecked) {
        if (spaceChecked == space) {
            return true;
        }
        if (block.isPassable()) {
            return checkSpaceNeededDirection(block.getRelative(direction), direction, space, ++spaceChecked);
        }
        return false;
    }

}
