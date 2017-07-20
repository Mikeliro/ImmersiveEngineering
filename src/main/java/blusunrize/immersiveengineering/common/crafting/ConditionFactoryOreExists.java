package blusunrize.immersiveengineering.common.crafting;

import blusunrize.immersiveengineering.api.ApiUtils;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/**
 * @author BluSunrize
 * @since 09.07.2017
 */
public class ConditionFactoryOreExists implements IConditionFactory
{
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json)
	{
		String key = JsonUtils.getString(json , "ore");
		boolean value = JsonUtils.getBoolean(json , "value", true);
		return () -> ApiUtils.isExistingOreName(key) == value;
	}
}
