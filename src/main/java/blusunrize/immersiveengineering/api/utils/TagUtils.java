/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.api.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;

public class TagUtils
{
	// These will be overriden on the client side, because TagCollectionManager doesn't work there
	public static ITagCollection<Item> ITEM_TAG_COLLECTION = TagCollectionManager.getManager().getItemTags();
	public static ITagCollection<Block> BLOCK_TAG_COLLECTION = TagCollectionManager.getManager().getBlockTags();

	public static ITag<Item> getItemTag(ResourceLocation key)
	{
		return ITEM_TAG_COLLECTION.get(key);
	}

	public static ITag<Block> getBlockTag(ResourceLocation key)
	{
		return BLOCK_TAG_COLLECTION.get(key);
	}

	public static Collection<ResourceLocation> getTagsForItem(Item item)
	{
		return ITEM_TAG_COLLECTION.getOwningTags(item);
	}

	public static Collection<ResourceLocation> getTagsForBlock(Block block)
	{
		return BLOCK_TAG_COLLECTION.getOwningTags(block);
	}

	public static boolean isInBlockOrItemTag(ItemStack stack, ResourceLocation oreName)
	{
		if(!isNonemptyBlockOrItemTag(oreName))
			return false;
		ITag<Item> itemTag = getItemTag(oreName);
		if(itemTag!=null&&itemTag.getAllElements().contains(stack.getItem()))
			return true;
		ITag<Block> blockTag = getBlockTag(oreName);
		return blockTag!=null&&blockTag.getAllElements()
				.stream()
				.map(IItemProvider::asItem)
				.anyMatch(i -> stack.getItem()==i);
	}

	public static boolean isNonemptyItemTag(ResourceLocation name)
	{
		ITag<Item> t = getItemTag(name);
		return t!=null&&!t.getAllElements().isEmpty();
	}

	public static boolean isNonemptyBlockTag(ResourceLocation name)
	{
		ITag<Block> t = getBlockTag(name);
		return t!=null&&!t.getAllElements().isEmpty();
	}

	public static boolean isNonemptyBlockOrItemTag(ResourceLocation name)
	{
		return isNonemptyBlockTag(name)||isNonemptyItemTag(name);
	}

	public static NonNullList<ItemStack> getItemsInTag(ResourceLocation name)
	{
		NonNullList<ItemStack> ret = NonNullList.create();
		addItemsInTag(ret, getItemTag(name));
		addItemsInTag(ret, getBlockTag(name));
		return ret;
	}

	private static <T extends IItemProvider> void addItemsInTag(NonNullList<ItemStack> out, ITag<T> in)
	{
		if(in!=null)
			in.getAllElements().stream()
					.map(ItemStack::new)
					.forEach(out::add);
	}

	public static boolean isInPrefixedTag(ItemStack stack, String componentType)
	{
		return getMatchingPrefix(stack, componentType)!=null;
	}

	public static String getMatchingPrefix(ItemStack stack, String... componentTypes)
	{
		for(ResourceLocation name : getMatchingTagNames(stack))
			for(String componentType : componentTypes)
				if(name.getPath().startsWith(componentType))
					return componentType;
		return null;
	}

	public static Collection<ResourceLocation> getMatchingTagNames(ItemStack stack)
	{
		Collection<ResourceLocation> ret = new HashSet<>(getTagsForItem(stack.getItem()));
		Block b = Block.getBlockFromItem(stack.getItem());
		if(b!=Blocks.AIR)
			ret.addAll(getTagsForBlock(b));
		return ret;
	}

	public static String[] getMatchingPrefixAndRemaining(ItemStack stack, String... componentTypes)
	{
		for(ResourceLocation name : getMatchingTagNames(stack))
		{
			for(String componentType : componentTypes)
				if(name.getPath().startsWith(componentType))
				{
					String material = name.getPath().substring(componentType.length());
					if(material.startsWith("/"))
						material = material.substring(1);
					if(material.length() > 0)
						return new String[]{componentType, material};
				}
		}
		return null;
	}

	public static boolean isIngot(ItemStack stack)
	{
		return isInPrefixedTag(stack, "ingots/");
	}

	public static boolean isPlate(ItemStack stack)
	{
		return isInPrefixedTag(stack, "plates/");
	}

}
