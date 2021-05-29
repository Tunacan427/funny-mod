package xyz.tunacan.funnymod

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.collection.DefaultedList

abstract class BaseMachineBlockEntity(blockEntityType: BlockEntityType<*>) : BlockEntity(blockEntityType), ImplementedInventory, ImplementedEnergyIo {
    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(2, ItemStack.EMPTY)

    override fun toTag(tag: CompoundTag): CompoundTag {
        super<BlockEntity>.toTag(tag)
        super<ImplementedEnergyIo>.toTag(tag)
        Inventories.toTag(tag, items)
        return tag
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag) {
        super<BlockEntity>.fromTag(state, tag)
        super<ImplementedEnergyIo>.fromTag(tag)
        Inventories.fromTag(tag, items)
    }

    override fun markDirty() {
        super<BlockEntity>.markDirty()
    }
}