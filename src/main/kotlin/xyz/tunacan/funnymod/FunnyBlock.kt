package xyz.tunacan.funnymod

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView

class FunnyBlock : Block(FabricBlockSettings.of(Material.METAL).strength(4.0f)), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView?): BlockEntity {
        return FunnyBlockEntity()
    }
}

class FunnyBlockEntity : BaseMachineBlockEntity(FUNNY_BLOCK_ENTITY) {
    override var storedEnergy = 0.0
    override val capacity = 10000.0
    override val maxInsertion = 20.0
    override val maxExtraction = 20.0
}
