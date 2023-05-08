package net.fabricmc.notnotmelonclient.util;

import java.lang.reflect.Method;

import net.fabricmc.notnotmelonclient.Main;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class DevUtil {
	public static String getDescriptorForClass(final Class<?> c)
    {
        if(c.isPrimitive())
        {
            if(c==byte.class)
                return "B";
            if(c==char.class)
                return "C";
            if(c==double.class)
                return "D";
            if(c==float.class)
                return "F";
            if(c==int.class)
                return "I";
            if(c==long.class)
                return "J";
            if(c==short.class)
                return "S";
            if(c==boolean.class)
                return "Z";
            if(c==void.class)
                return "V";
            throw new RuntimeException("Unrecognized primitive "+c);
        }
        if(c.isArray()) return c.getName().replace('.', '/');
        return ('L'+c.getName()+';').replace('.', '/');
    }

    public static String getMethodDescriptor(Method m)
    {
        String s=m.getName() + "(";
        for(final Class<?> c: m.getParameterTypes())
            s+=getDescriptorForClass(c);
        s+=')';
        return s+getDescriptorForClass(m.getReturnType());
    }

    public static boolean showDescriptor = false;
    public static void logMethodDescriptor()
    {
        if (showDescriptor) {
            try {
                Main.LOGGER.info("**** METHOD DESCRIPTOR");
                Main.LOGGER.info(getMethodDescriptor(ItemRenderer.class.getMethod(
                    "renderGuiItemModel",
                    ItemStack.class, ModelTransformationMode.class, boolean.class, MatrixStack.class, VertexConsumerProvider.class, int.class, int.class, BakedModel.class
                )));
            } catch (NoSuchMethodException e) {}
        }
    }
}
