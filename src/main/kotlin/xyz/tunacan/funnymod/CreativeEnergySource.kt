package xyz.tunacan.funnymod

import dev.technici4n.fasttransferlib.api.energy.EnergyIo
import dev.technici4n.fasttransferlib.api.energy.EnergyMovement
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

class CreativeEnergySourceBlock : Block(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 3600000.0F)), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView?): BlockEntity {
        return CreativeEnergySourceBlockEntity()
    }

    override fun neighborUpdate(state: BlockState?, world: World?, pos: BlockPos?, block: Block?, fromPos: BlockPos?, notify: Boolean) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify)
        (world?.getBlockEntity(pos) as CreativeEnergySourceBlockEntity).populateNeighbors(world)
    }
}

class CreativeEnergySourceBlockEntity : BlockEntity(CREATIVE_ENERGY_SOURCE_BLOCK_ENTITY), ImplementedEnergyIo, Tickable {
    override var storedEnergy = Double.MAX_VALUE - 1.0
    override val capacity = Double.MAX_VALUE - 1.0
    override val maxInsertion = 0.0
    override val maxExtraction = Double.MAX_VALUE - 1.0
    private val neighborEnergies = MutableList<EnergyIo?>(6) { null }
    private var neighborsPopulated = false

    override fun toTag(tag: CompoundTag): CompoundTag {
        super<BlockEntity>.toTag(tag)
        return tag
    }

    override fun tick() {
        storedEnergy = Double.MAX_VALUE - 1.0
        if (!neighborsPopulated) {
            if (world?.isClient == false) populateNeighbors(world!!)
        } else neighborEnergies.forEach {
            if (it != null) EnergyMovement.move(this, it, maxExtraction)
        }
    }

    fun populateNeighbors(world: World) {
        Direction.values().forEachIndexed { i, direction ->
            val p = pos.offset(direction)
            if (world.getBlockState(p).block.hasBlockEntity()) {
                val entity = world.getBlockEntity(p)
                if (entity is EnergyIo) neighborEnergies[i] = entity as EnergyIo
            }
        }
        neighborsPopulated = true
    }
}
