package com.pepej.papi.extension

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

//items
inline fun <reified T : ItemMeta> ItemStack.meta(block: T.() -> Unit): ItemStack = apply { itemMeta = (itemMeta as? T)?.apply(block) ?: itemMeta }
inline fun item(material: Material, amount: Int = 1, data: Short = 0, meta: ItemMeta.() -> Unit = {}): ItemStack = ItemStack(material, amount, data).meta(meta)
//chat
fun String.translateColor(code: Char = '&'): String = ChatColor.translateAlternateColorCodes(code, this)
operator fun String.unaryPlus(): String = translateColor()
inline fun <reified T : BaseComponent> T.hover(hoverEvent: HoverEvent) = apply { this.hoverEvent = hoverEvent }
inline fun <reified T : BaseComponent> T.showText(component: BaseComponent) = hover(HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(component)))
//event
inline fun <reified T : Event> KListener<*>.event(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        crossinline block: T.() -> Unit
) = event(plugin, priority, ignoreCancelled, block)
inline fun <reified T : Event> Listener.event(
        plugin: Plugin,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        crossinline block: T.() -> Unit
) {
    Bukkit.getServer().pluginManager.registerEvent(
            T::class.java,
            this,
            priority,
            {_, e ->
                (e as? T)?.block()
            },
            plugin,
            ignoreCancelled
    )
}

inline fun Plugin.events(block: KListener<*>.() -> Unit) = InlineKListener(this).apply(block)
interface KListener<T : Plugin> : Listener { val plugin: T }
inline class InlineKListener(override val plugin: Plugin) : KListener<Plugin>