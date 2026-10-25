package com.example.teamval.data.fake

import com.example.teamval.domain.model.CosmeticBinding
import com.example.teamval.domain.model.EquipField
import com.example.teamval.domain.model.EquipTarget
import com.example.teamval.domain.model.ItemCategory
import com.example.teamval.domain.model.ItemRarity
import com.example.teamval.domain.model.ShopItem

/**
 * Catálogo demo de la tienda de la etapa 5. Los precios en monedas están
 * pensados para que un perfil bonito se consiga sin pagar: el bono inicial y
 * las recompensas alcanzan varios artículos gratis.
 *
 * Los artículos premium solo declaran precio en pesos chilenos y están
 * bloqueados ("Próximamente"): no existe ninguna pasarela de pago en esta etapa.
 */
object DemoShopCatalog {

    /** Monedas de bienvenida otorgadas una sola vez al crear el monedero. */
    const val StartingBonus = 500

    private fun profile(field: EquipField, variant: String) =
        CosmeticBinding(EquipTarget.Profile, field, variant)

    private fun team(field: EquipField, variant: String) =
        CosmeticBinding(EquipTarget.Team, field, variant)

    val items: List<ShopItem> = listOf(
        // ---- Marcos de avatar -------------------------------------------------
        ShopItem(
            id = "frame_lavanda",
            name = "Marco Lavanda",
            description = "Anillo lavanda suave alrededor de tu avatar, en la tinta de Teamval.",
            category = ItemCategory.AvatarFrame,
            rarity = ItemRarity.Common,
            priceCoins = 100,
            binding = profile(EquipField.AvatarFrame, "Lavanda"),
            featured = true
        ),
        ShopItem(
            id = "frame_solar",
            name = "Marco Solar",
            description = "Aro cálido con degradado de atardecer para perfiles con energía.",
            category = ItemCategory.AvatarFrame,
            rarity = ItemRarity.Rare,
            priceCoins = 180,
            binding = profile(EquipField.AvatarFrame, "Solar")
        ),
        ShopItem(
            id = "frame_neon",
            name = "Marco Neón Doble",
            description = "Doble aro neón que dibuja un halo doble sobre tu avatar.",
            category = ItemCategory.AvatarFrame,
            rarity = ItemRarity.Epic,
            priceCoins = 320,
            binding = profile(EquipField.AvatarFrame, "Neon")
        ),

        // ---- Fondos de perfil -------------------------------------------------
        ShopItem(
            id = "bg_pastel",
            name = "Fondo Pastel",
            description = "Fondo en pasteles claros, limpio y luminoso para tu tarjeta.",
            category = ItemCategory.ProfileBackground,
            rarity = ItemRarity.Common,
            priceCoins = 150,
            binding = profile(EquipField.ProfileBackground, "Pastel"),
            featured = true
        ),
        ShopItem(
            id = "bg_aurora",
            name = "Fondo Aurora Suave",
            description = "Degradado auroral frío que acompaña tu titular sin gritar.",
            category = ItemCategory.ProfileBackground,
            rarity = ItemRarity.Rare,
            priceCoins = 260,
            binding = profile(EquipField.ProfileBackground, "Aurora")
        ),
        ShopItem(
            id = "bg_galaxia",
            name = "Fondo Galaxia Violeta",
            description = "Noche violeta con destellos: el fondo más llamativo de la tienda.",
            category = ItemCategory.ProfileBackground,
            rarity = ItemRarity.Epic,
            priceCoins = 420,
            binding = profile(EquipField.ProfileBackground, "Galaxia")
        ),

        // ---- Banners ----------------------------------------------------------
        ShopItem(
            id = "banner_diurno",
            name = "Banner Diurno",
            description = "Franja superior en tonos de día para tu perfil.",
            category = ItemCategory.Banner,
            rarity = ItemRarity.Common,
            priceCoins = 120,
            binding = profile(EquipField.ProfileBanner, "Diurno")
        ),
        ShopItem(
            id = "banner_noche",
            name = "Banner Nocturno",
            description = "Franja nocturna índigo con luna creciente.",
            category = ItemCategory.Banner,
            rarity = ItemRarity.Rare,
            priceCoins = 240,
            binding = profile(EquipField.ProfileBanner, "Noche")
        ),
        ShopItem(
            id = "banner_estelar",
            name = "Banner Estelar",
            description = "Cielo violeta con destellos estelares en la cabecera de tu perfil.",
            category = ItemCategory.Banner,
            rarity = ItemRarity.Epic,
            priceCoins = 380,
            binding = profile(EquipField.ProfileBanner, "Estelar")
        ),

        // ---- Insignias --------------------------------------------------------
        ShopItem(
            id = "badge_estrella",
            name = "Insignia de Estrella",
            description = "Insignia de estrella dorada que se muestra junto a tus logros.",
            category = ItemCategory.Badge,
            rarity = ItemRarity.Common,
            priceCoins = 80,
            binding = profile(EquipField.ProfileBadge, "Estrella"),
            featured = true
        ),
        ShopItem(
            id = "badge_comunidad",
            name = "Insignia Comunidad",
            description = "Distintivo para quienes hacen comunidad en cada partida.",
            category = ItemCategory.Badge,
            rarity = ItemRarity.Rare,
            priceCoins = 200,
            binding = profile(EquipField.ProfileBadge, "Comunidad")
        ),
        ShopItem(
            id = "badge_veterano",
            name = "Insignia Veterana",
            description = "Insignia de veteranía con ribete brillante.",
            category = ItemCategory.Badge,
            rarity = ItemRarity.Epic,
            priceCoins = 350,
            binding = profile(EquipField.ProfileBadge, "Veterano")
        ),

        // ---- Stickers ---------------------------------------------------------
        ShopItem(
            id = "sticker_gg",
            name = "Sticker GG",
            description = "Sticker de cortesía para saludar al final de cada mapa.",
            category = ItemCategory.Sticker,
            rarity = ItemRarity.Common,
            priceCoins = 60,
            binding = profile(EquipField.ProfileSticker, "GG")
        ),
        ShopItem(
            id = "sticker_fuego",
            name = "Sticker En Llamas",
            description = "Sticker de fuego para perfiles que vienen racha arriba.",
            category = ItemCategory.Sticker,
            rarity = ItemRarity.Rare,
            priceCoins = 170,
            binding = profile(EquipField.ProfileSticker, "Fuego")
        ),

        // ---- Mascotas decorativas --------------------------------------------
        ShopItem(
            id = "pet_bruno",
            name = "Mascota Bruno",
            description = "Gato decorativo que acompaña la esquina de tu perfil. Solo estética.",
            category = ItemCategory.Pet,
            rarity = ItemRarity.Epic,
            priceCoins = 400,
            binding = profile(EquipField.ProfilePet, "Bruno"),
            featured = true
        ),
        ShopItem(
            id = "pet_nube",
            name = "Mascota Nube",
            description = "Nube sonriente flotante, suave y silenciosa.",
            category = ItemCategory.Pet,
            rarity = ItemRarity.Rare,
            priceCoins = 300,
            binding = profile(EquipField.ProfilePet, "Nube")
        ),

        // ---- Tarjetas de jugador ---------------------------------------------
        ShopItem(
            id = "card_especial",
            name = "Tarjeta Especial",
            description = "Tarjeta de jugador con ribetes y acentos destacados.",
            category = ItemCategory.PlayerCard,
            rarity = ItemRarity.Rare,
            priceCoins = 250,
            binding = profile(EquipField.PlayerCard, "Especial"),
            featured = true
        ),
        ShopItem(
            id = "card_cambio",
            name = "Tarjeta Cambio",
            description = "Tarjeta con franjas diagonales de estilo esports.",
            category = ItemCategory.PlayerCard,
            rarity = ItemRarity.Epic,
            priceCoins = 390,
            binding = profile(EquipField.PlayerCard, "Cambio")
        ),

        // ---- Temas visuales ---------------------------------------------------
        ShopItem(
            id = "theme_cosmico",
            name = "Tema Cósmico",
            description = "Tema global con acento violeta, fondo aurora y tipografía moderna.",
            category = ItemCategory.VisualTheme,
            rarity = ItemRarity.Rare,
            priceCoins = 140,
            binding = CosmeticBinding(EquipTarget.Appearance, EquipField.VisualTheme, "Cosmico"),
            featured = true
        ),
        ShopItem(
            id = "theme_aqualux",
            name = "Tema Aqualux",
            description = "Tema global con acento cian, fondo cuadrícula y tipografía mono.",
            category = ItemCategory.VisualTheme,
            rarity = ItemRarity.Epic,
            priceCoins = 300,
            binding = CosmeticBinding(EquipTarget.Appearance, EquipField.VisualTheme, "Aqualux")
        ),

        // ---- Decoración de equipos -------------------------------------------
        ShopItem(
            id = "team_deco_atardecer",
            name = "Decoración Atardecer",
            description = "Cosmético de equipo: fondo cálido para la ficha de tu equipo.",
            category = ItemCategory.TeamDecoration,
            rarity = ItemRarity.Common,
            priceCoins = 190,
            binding = team(EquipField.TeamBackground, "Atardecer")
        ),
        ShopItem(
            id = "team_badge_flame",
            name = "Insignia de equipo Llama",
            description = "Cosmético de equipo: insignia de llama para la identidad del equipo.",
            category = ItemCategory.TeamDecoration,
            rarity = ItemRarity.Rare,
            priceCoins = 240,
            binding = team(EquipField.TeamBadge, "Flame")
        ),

        // ---- Marcos de logos de equipos ---------------------------------------
        ShopItem(
            id = "team_logo_orbit",
            name = "Marco de logo Órbita",
            description = "Cosmético de equipo: anillo orbital doble alrededor del logo.",
            category = ItemCategory.TeamLogoFrame,
            rarity = ItemRarity.Rare,
            priceCoins = 210,
            binding = team(EquipField.TeamFrame, "Orbit")
        ),
        ShopItem(
            id = "team_logo_prism",
            name = "Marco de logo Prisma",
            description = "Cosmético de equipo: borde prisma en degradado de los colores del equipo.",
            category = ItemCategory.TeamLogoFrame,
            rarity = ItemRarity.Epic,
            priceCoins = 360,
            binding = team(EquipField.TeamFrame, "Prism")
        ),

        // ---- Paquetes cosméticos ----------------------------------------------
        ShopItem(
            id = "bundle_inicio",
            name = "Paquete Inicio",
            description = "Marco Lavanda + Fondo Pastel + Insignia de Estrella en un solo paquete.",
            category = ItemCategory.Bundle,
            rarity = ItemRarity.Legendary,
            priceCoins = 500,
            contains = listOf("frame_lavanda", "bg_pastel", "badge_estrella"),
            featured = true
        ),

        // ---- Artículos premium (sin pagos reales en esta etapa) ---------------
        ShopItem(
            id = "prem_frame_animado",
            name = "Marco Animado",
            description = "Marco de avatar animado. Compra premium próximamente.",
            category = ItemCategory.AvatarFrame,
            rarity = ItemRarity.Legendary,
            premiumPriceClp = 990
        ),
        ShopItem(
            id = "prem_bg_fondo",
            name = "Fondo Premium",
            description = "Fondo de perfil premium en alta resolución. Compra premium próximamente.",
            category = ItemCategory.ProfileBackground,
            rarity = ItemRarity.Epic,
            premiumPriceClp = 1490
        ),
        ShopItem(
            id = "prem_sticker_pack",
            name = "Paquete de Stickers",
            description = "Pack de stickers premium. Compra premium próximamente.",
            category = ItemCategory.Bundle,
            rarity = ItemRarity.Epic,
            premiumPriceClp = 1990
        ),
        ShopItem(
            id = "prem_theme_pack",
            name = "Tema Completo Premium",
            description = "Tema visual completo premium. Compra premium próximamente.",
            category = ItemCategory.VisualTheme,
            rarity = ItemRarity.Legendary,
            premiumPriceClp = 2990
        )
    )

    fun find(id: String): ShopItem? = items.find { it.id == id }
}
