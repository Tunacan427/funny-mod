@file:JvmName("Util")
package xyz.tunacan.funnymod

import dev.technici4n.fasttransferlib.api.Simulation
import dev.technici4n.fasttransferlib.api.energy.EnergyIo
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.collection.DefaultedList
import kotlin.math.abs

/**
 * A simple `Inventory` implementation with only default methods + an item list getter.
 *
 * Originally by Juuz
 */
interface ImplementedInventory : Inventory {
    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    val items: DefaultedList<ItemStack>

    /**
     * Returns the inventory size.
     */
    override fun size(): Int {
        return items.size
    }

    /**
     * Checks if the inventory is empty.
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    override fun isEmpty(): Boolean {
        for (i in 0 until size()) {
            val stack = getStack(i)
            if (!stack.isEmpty) {
                return false
            }
        }
        return true
    }

    /**
     * Retrieves the item in the slot.
     */
    override fun getStack(slot: Int): ItemStack {
        return items[slot]
    }

    /**
     * Removes items from an inventory slot.
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     * takes all items in that slot.
     */
    override fun removeStack(slot: Int, count: Int): ItemStack {
        val result = Inventories.splitStack(items, slot, count)
        if (!result.isEmpty) {
            markDirty()
        }
        return result
    }

    /**
     * Removes all items from an inventory slot.
     * @param slot The slot to remove from.
     */
    override fun removeStack(slot: Int): ItemStack {
        return Inventories.removeStack(items, slot)
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     * this inventory ([Inventory.getMaxCountPerStack]),
     * it gets resized to this inventory's maximum amount.
     */
    override fun setStack(slot: Int, stack: ItemStack) {
        items[slot] = stack
        if (stack.count > maxCountPerStack) {
            stack.count = maxCountPerStack
        }
    }

    /**
     * Clears the inventory.
     */
    override fun clear() {
        items.clear()
    }

    /**
     * Marks the state as dirty.
     * Must be called after changes in the inventory, so that the game can properly save
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    override fun markDirty() {
        // Override if you want behavior.
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return true
    }

    companion object {
        /**
         * Creates an inventory from the item list.
         */
        fun of(items: DefaultedList<ItemStack>): ImplementedInventory {
            return object : ImplementedInventory {
                override val items: DefaultedList<ItemStack> = items
            }
        }

        /**
         * Creates a new inventory with the specified size.
         */
        fun ofSize(size: Int): ImplementedInventory {
            return of(DefaultedList.ofSize(size, ItemStack.EMPTY))
        }
    }
}

interface ImplementedEnergyIo : EnergyIo {
    var storedEnergy: Double
    val capacity: Double
    val maxInsertion: Double
    val maxExtraction: Double

    override fun getEnergy(): Double {
        return storedEnergy
    }

    override fun getEnergyCapacity(): Double {
        return capacity
    }

    override fun supportsInsertion(): Boolean {
        return maxInsertion > 0
    }

    override fun insert(amount: Double, simulation: Simulation): Double {
        val inserted = amount.coerceAtMost(maxInsertion).coerceAtMost(capacity - storedEnergy)
        incrementEnergy(inserted, simulation)
        return simplify(amount - inserted)
    }

    override fun supportsExtraction(): Boolean {
        return maxExtraction > 0
    }

    override fun extract(maxAmount: Double, simulation: Simulation): Double {
        val extracted = maxAmount.coerceAtMost(maxExtraction).coerceAtMost(storedEnergy)
        incrementEnergy(-extracted, simulation)
        return simplify(extracted)
    }

    private fun incrementEnergy(increment: Double, simulation: Simulation) {
        val newEnergy: Double = storedEnergy + increment
        if (simulation.isActing) {
            storedEnergy = simplify(newEnergy)
        }
    }

    fun toTag(tag: CompoundTag): CompoundTag {
        tag.putDouble("energy", storedEnergy)
        return tag
    }

    fun fromTag(tag: CompoundTag) {
        storedEnergy = tag.getDouble("energy")
    }
}

fun simplify(number: Double): Double {
    return if (abs(number) < 1e-9) 0.0 else number
}