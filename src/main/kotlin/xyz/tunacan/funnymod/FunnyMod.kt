@file:JvmName("FunnyMod")
package xyz.tunacan.funnymod

import net.devtech.arrp.api.RRPCallback
import net.devtech.arrp.api.RuntimeResourcePack
import net.devtech.arrp.json.blockstate.JState
import net.devtech.arrp.json.loot.*
import net.devtech.arrp.json.models.JModel
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroup
import net.minecraft.resource.ResourcePack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

const val MOD_ID = "funnymod"
val RESOURCE_PACK: RuntimeResourcePack = RuntimeResourcePack.create(ids("resource_pack"))

val FUNNY_BLOCK = FunnyBlock()
lateinit var FUNNY_BLOCK_ENTITY: BlockEntityType<FunnyBlockEntity>

val CREATIVE_ENERGY_SOURCE_BLOCK = CreativeEnergySourceBlock()
lateinit var CREATIVE_ENERGY_SOURCE_BLOCK_ENTITY: BlockEntityType<CreativeEnergySourceBlockEntity>

@Suppress("unused")
fun init() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    Registry.register(Registry.BLOCK, id("funny_block"), FUNNY_BLOCK)
    Registry.register(Registry.ITEM, id("funny_block"), BlockItem(FUNNY_BLOCK, FabricItemSettings().group(ItemGroup.MISC)))
    FUNNY_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("funny_block_entity"), BlockEntityType.Builder.create({ FunnyBlockEntity() }, FUNNY_BLOCK).build(null))
    defaultBlockResources(RESOURCE_PACK, "funny_block")

    Registry.register(Registry.BLOCK, id("creative_energy_source"), CREATIVE_ENERGY_SOURCE_BLOCK)
    Registry.register(Registry.ITEM, id("creative_energy_source"), BlockItem(CREATIVE_ENERGY_SOURCE_BLOCK, FabricItemSettings().group(ItemGroup.MISC)))
    CREATIVE_ENERGY_SOURCE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("creative_energy_source_block_entity"), BlockEntityType.Builder.create({ CreativeEnergySourceBlockEntity() }, CREATIVE_ENERGY_SOURCE_BLOCK).build(null))
    defaultBlockResources(RESOURCE_PACK, "creative_energy_source")

    RRPCallback.BEFORE_VANILLA.register(RRPCallback { a: MutableList<ResourcePack> -> a.add(RESOURCE_PACK) })
}

fun id(path: String): Identifier {
    return Identifier(MOD_ID, path)
}

fun ids(path: String): String {
    return "$MOD_ID:$path"
}

fun defaultBlockResources(resourcePack: RuntimeResourcePack, name: String) {
    resourcePack.addModel(JModel.model("block/cube_all").textures(JModel.textures().`var`("all", ids("block/$name"))), id("block/$name"))
    resourcePack.addModel(JModel.model(id("block/$name")), id("item/$name"))
    resourcePack.addBlockState(JState.state(JState.variant(JState.model(id("block/$name")))), id(name))
    resourcePack.addLootTable(id("blocks/$name"),
        JLootTable("minecraft:block")
            .pool(JPool()
                .rolls(1)
                .entry(JEntry()
                    .type("minecraft:item")
                    .function(JFunction("minecraft:copy_name")
                        .parameter("source", "block_entity"))
                    .name(ids(name)))
                .condition(JCondition("minecraft:survives_explosion"))))
}