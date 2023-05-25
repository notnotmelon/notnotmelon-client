package net.fabricmc.notnotmelonclient.fishing;

import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.util.ChatTrigger;
import net.fabricmc.notnotmelonclient.util.MathUtil;
import net.fabricmc.notnotmelonclient.util.SoundEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import oshi.util.tuples.Triplet;

public class Fishing implements ChatTrigger, SoundEvent {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static long castTime = -1;
    private static Vec3d yawVector;
    private static final Text[] approachText = new Text[]{
        Text.literal(".  ").formatted(Formatting.YELLOW).formatted(Formatting.BOLD),
        Text.literal(".. ").formatted(Formatting.YELLOW).formatted(Formatting.BOLD),
        Text.literal("...").formatted(Formatting.YELLOW).formatted(Formatting.BOLD)
    };
    private static final Text catchText = Text.literal("!!!").formatted(Formatting.RED).formatted(Formatting.BOLD);

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

    private static void reset() {
        castTime = -1;
    }
    @Override public void onSound(PlaySoundS2CPacket packet, String soundName) {
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

        client.inGameHud.setTitleTicks(0, 10, 5);
        client.inGameHud.setTitle(catchText);
        reset();
    }

    public static boolean isMyBobber(FishingBobberEntity bobber, PlaySoundS2CPacket packet) {
        Vec3d soundOffset = bobber.getPos().subtract(packet.getX(), 0, packet.getZ());

        // Calculate the angle between rod cast and sound position
        // Return if the angle is outside our "field of view"
        double angle = Math.abs(yawVector.x * soundOffset.z - yawVector.z * soundOffset.x);
        if (angle > 0.2) return false;

        // Finally, we should also check if the sound is coming from the same direction as the bobber
        return Math.abs(yawVector.dotProduct(soundOffset)) < 0.2;
    }

    public static final Triplet<String, String, Formatting>[] rareCreatures = new Triplet[]{
        new Triplet<>("You spot a Golden Fish surface from beneath the lava!", "Golden Fish!", Formatting.GOLD),
        new Triplet<>("The Water Hydra has come to test your strength.", "Water Hydra!", Formatting.BLUE),
        new Triplet<>("The Sea Emperor arises from the depths.", "Sea Emperor!", Formatting.YELLOW),
        new Triplet<>("A Zombie miner surfaces!", "Zombie Miner!", Formatting.GREEN),
        new Triplet<>("You hear a massive rumble as Thunder emerges.", "Thunder!", Formatting.LIGHT_PURPLE),
        new Triplet<>("You have angered a legendary creature... Lord Jawbus has arrived", "Lord Jawbus!", Formatting.RED),
        new Triplet<>("WOAH! A Plhlegblast appeared.", "Plhlegblast!", Formatting.DARK_GRAY),
        new Triplet<>("The spirit of a long lost Phantom Fisherman has come to haunt you.", "Phantom Fisherman!", Formatting.AQUA),
        new Triplet<>("This can't be! The manifestation of death himself!", "Grim Reaper!", Formatting.BLACK),
        new Triplet<>("What is this creature!?", "Yeti!", Formatting.WHITE),
        new Triplet<>("A Reindrake forms from the depths.", "Reindrake!", Formatting.DARK_PURPLE),
        new Triplet<>("Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!", "Great White Shark!", Formatting.DARK_RED)
    };
    @Override public ActionResult onMessage(Text message, String asString) {
        if (!Config.getConfig().legendaryCatchWarning) return ActionResult.PASS;
        String messageString = message.getString();
        for (Triplet<String, String, Formatting> triplet : rareCreatures) {
            String rareCreatureMessage = triplet.getA();
            if (!messageString.equals(rareCreatureMessage)) continue;
            String creatureName = triplet.getB();
            Formatting formatting = triplet.getC();
            Text title = Text.literal(creatureName).formatted(Formatting.BOLD, formatting);

            client.inGameHud.setTitleTicks(0, 20, 5);
            client.inGameHud.setTitle(title);
            break;
        }
        return ActionResult.PASS;
    }
}
