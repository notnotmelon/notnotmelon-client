package net.fabricmc.notnotmelonclient.fishing;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.events.ChangeLobby;
import net.fabricmc.notnotmelonclient.events.ChatTrigger;
import net.fabricmc.notnotmelonclient.events.SoundEvent;
import net.fabricmc.notnotmelonclient.util.MathUtil;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class Fishing {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static long castTime = -1;
    public static long goldfishStreak = -1;
    private static Vec3d yawVector;
    private static final Text catchText = Text.literal("!!!").formatted(Formatting.RED).formatted(Formatting.BOLD);
    public static long goldenFishTimer = -1;
    public static void registerEvents() {
        UseItemCallback.EVENT.register(Fishing::castRod);
        SoundEvent.EVENT.register(Fishing::onSound);
        ChatTrigger.EVENT.register(Fishing::onMessage);
        ChangeLobby.EVENT.register(Fishing::resetGoldfish);
        ChangeLobby.EVENT.register(Fishing::reset);
        WorldRenderEvents.BEFORE_ENTITIES.register(Fishing::render);
        Scheduler.scheduleCyclic(Fishing::updateBobberTimer, 2);
    }

    private static Text bobberTimer;
    private static float bobberTimerOffset;
    private static void updateBobberTimer() {
        ClientPlayerEntity player = client.player;
        if (!Util.isSkyblock
            || !Config.getConfig().bobberTimer
            || player == null
            || player.fishHook == null
        ) {
            bobberTimer = null;
            return;
        }
        float age = player.fishHook.age / 20f;
        Formatting color = age < 20 ? Formatting.YELLOW : Formatting.RED;
        bobberTimer = Text.literal(String.format("%.1f", age)).formatted(Formatting.BOLD).formatted(color);
        bobberTimerOffset = -client.textRenderer.getWidth(bobberTimer) / 2f;
    }

    public static TypedActionResult<ItemStack> castRod(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof FishingRodItem) {
            if (player.fishHook == null) {
                castTime = System.currentTimeMillis();
                // calculate a vector based on the player's look direction @ time of cast
                // this is our "field of view" that the bobber should land in
                double yaw = Math.toRadians(player.getYaw());
                yawVector = MathUtil.normalize(yaw);
            } else {
                reset();
            }
        }
        return TypedActionResult.pass(stack);
    }

    private static void updateGoldenFishTimer() {
        if (!"Crimson Isle".equals(Util.getLocation())) return;

        long time = System.currentTimeMillis();
        goldfishStreak = time;
        if (goldenFishTimer == -1)
            goldenFishTimer = time;
    }

    private static void reset() {
        castTime = -1;
    }
    public static void onSound(PlaySoundS2CPacket packet, String soundName) {
        if (!soundName.equals("entity.player.splash") && !soundName.equals("entity.generic.splash")) return;

        // we are assuming that you will never reel 1sec after casting.
        if (castTime == -1 || System.currentTimeMillis() < castTime + 1000) return;

        ClientPlayerEntity player = client.player;
        if (player == null || player.fishHook == null) {
            reset();
            return;
        }

        Vec3d soundOffset = player.fishHook.getPos().subtract(packet.getX(), 0, packet.getZ());

        // Calculate the angle between rod cast and sound position
        // Return if the angle is outside our "field of view"
        double angle = Math.abs(yawVector.x * soundOffset.z - yawVector.z * soundOffset.x);
        if (angle > 0.2) return;

        // Finally, we should also check if the sound is coming from the same direction as the bobber
        if (Math.abs(yawVector.dotProduct(soundOffset)) > 0.2) return;

        if (Config.getConfig().showWhenToReel) {
            client.inGameHud.setTitleTicks(0, 10, 5);
            client.inGameHud.setTitle(catchText);
        }

        if (Config.getConfig().goldenFishTimer)
            updateGoldenFishTimer();

        reset();
    }

    private record RareCreature(String spawnMessage, String creatureName, Formatting formatting) {}
    public static final RareCreature[] rareCreatures = new RareCreature[]{
        new RareCreature("You spot a Golden Fish surface from beneath the lava!", "Golden Fish!", Formatting.GOLD),
        new RareCreature("The Water Hydra has come to test your strength.", "Water Hydra!", Formatting.BLUE),
        new RareCreature("The Sea Emperor arises from the depths.", "Sea Emperor!", Formatting.YELLOW),
        new RareCreature("A Zombie miner surfaces!", "Zombie Miner!", Formatting.GREEN),
        new RareCreature("You hear a massive rumble as Thunder emerges.", "Thunder!", Formatting.LIGHT_PURPLE),
        new RareCreature("You have angered a legendary creature... Lord Jawbus has arrived", "Lord Jawbus!", Formatting.RED),
        new RareCreature("WOAH! A Plhlegblast appeared.", "Plhlegblast!", Formatting.DARK_GRAY),
        new RareCreature("The spirit of a long lost Phantom Fisherman has come to haunt you.", "Phantom Fisherman!", Formatting.AQUA),
        new RareCreature("This can't be! The manifestation of death himself!", "Grim Reaper!", Formatting.BLACK),
        new RareCreature("What is this creature!?", "Yeti!", Formatting.WHITE),
        new RareCreature("A Reindrake forms from the depths.", "Reindrake!", Formatting.DARK_PURPLE),
        new RareCreature("Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!", "Great White Shark!", Formatting.DARK_RED)
    };
    public static ActionResult onMessage(Text message, String asString) {
        if (!Config.getConfig().legendaryCatchWarning) return ActionResult.PASS;

        String messageString = message.getString();
        for (RareCreature creature : rareCreatures) {
            if (!messageString.equals(creature.spawnMessage)) continue;
            String creatureName = creature.creatureName;
            Text title = Text.literal(creatureName).formatted(Formatting.BOLD, creature.formatting);

            client.inGameHud.setTitleTicks(0, 20, 5);
            client.inGameHud.setTitle(title);

            if (creatureName.equals("Golden Fish!")) resetGoldfish();
            break;
        }
        return ActionResult.PASS;
    }

    public static void resetGoldfish() {
        goldfishStreak = -1;
        goldenFishTimer = -1;
    }

    private static void render(WorldRenderContext worldRenderContext) {
        if (bobberTimer == null || client.player == null || client.player.fishHook == null) return;
        ClientPlayerEntity player = client.player;
        FishingBobberEntity fishHook = player.fishHook;
        TextRenderer textRenderer = client.textRenderer;
        MatrixStack matrices = worldRenderContext.matrixStack();
        VertexConsumerProvider vertexConsumers = worldRenderContext.consumers();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        Vec3d cameraPos = dispatcher.camera.getPos();
        Vec3d interpolationVector = RenderUtil.interpolationVector(fishHook);

        matrices.push();
        matrices.translate(interpolationVector.x - cameraPos.x, waterLevel(fishHook, interpolationVector.y) - cameraPos.y + 0.6, interpolationVector.z - cameraPos.z);
        matrices.multiply(dispatcher.getRotation());
        matrices.scale(-0.035f, -0.035f, 0.035f);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        textRenderer.draw(bobberTimer, bobberTimerOffset, 0, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, 255);
        matrices.pop();
    }

    public static double waterLevel(FishingBobberEntity fishHook, double fallback) {
        BlockPos blockPos = fishHook.getBlockPos();
        ClientWorld world = (ClientWorld) fishHook.world;
        FluidState fluidState = world.getFluidState(blockPos);
        if (!fluidState.isEmpty())
            return fluidState.getHeight(world, blockPos) + blockPos.getY();
        return fallback;
    }
}
