/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.util.shapes;

import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CachedShapesWithTransform<ShapeKey, TransformKey> extends CachedVoxelShapes<Pair<ShapeKey, TransformKey>>
{
	public CachedShapesWithTransform(
			Function<ShapeKey, List<AxisAlignedBB>> creator,
			BiFunction<TransformKey, AxisAlignedBB, AxisAlignedBB> transform)
	{
		super(p -> {
			List<AxisAlignedBB> base = creator.apply(p.getLeft());
			List<AxisAlignedBB> ret = new ArrayList<>(base.size());
			for(AxisAlignedBB aabb : base)
				ret.add(transform.apply(p.getRight(), aabb));
			return ret;
		});
	}

	public VoxelShape get(ShapeKey shapeKey, TransformKey transformKey)
	{
		return get(Pair.of(shapeKey, transformKey));
	}

	//TODO proper types
	public static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>>
	createForMultiblock(Function<BlockPos, List<AxisAlignedBB>> create)
	{
		return new CachedShapesWithTransform<>(create,
				(key, box) -> {
					AxisAlignedBB mirrored = box;
					if(key.getRight())
						mirrored = new AxisAlignedBB(
								1-box.minX,
								box.minY,
								box.minZ,
								1-box.maxX,
								box.maxY,
								box.maxZ
						);
					return Utils.transformAABB(mirrored, key.getLeft());
				});
	}
}
