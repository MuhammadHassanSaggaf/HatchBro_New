package com.hatchbro.app.data.local

object PrepopulateData {
    data class InitialSpecies(
        val name: String,
        val breeds: List<InitialBreed>
    )

    data class InitialBreed(
        val name: String,
        val incubationDays: Int,
        val lockdownDays: Int
    )

    val initialData = listOf(
        InitialSpecies(
            name = "Chicken",
            breeds = listOf(
                InitialBreed("Rhode Island Red", 21, 3),
                InitialBreed("Leghorn", 21, 3),
                InitialBreed("Plymouth Rock", 21, 3),
                InitialBreed("Wyandotte", 21, 3),
                InitialBreed("Orpington", 21, 3),
                InitialBreed("Sussex", 21, 3),
                InitialBreed("Brahma", 21, 3),
                InitialBreed("Cochin", 21, 3),
                InitialBreed("Silkie", 21, 3),
                InitialBreed("Marans", 21, 3),
                InitialBreed("Ameraucana", 21, 3),
                InitialBreed("Australorp", 21, 3),
                InitialBreed("Barnevelder", 21, 3),
                InitialBreed("Faverolle", 21, 3),
                InitialBreed("Polish", 21, 3)
            )
        ),
        InitialSpecies(
            name = "Duck",
            breeds = listOf(
                InitialBreed("Pekin", 28, 3),
                InitialBreed("Muscovy", 35, 3),
                InitialBreed("Khaki Campbell", 28, 3),
                InitialBreed("Indian Runner", 28, 3),
                InitialBreed("Rouen", 28, 3),
                InitialBreed("Cayuga", 28, 3),
                InitialBreed("Swedish", 28, 3),
                InitialBreed("Buff Orpington", 28, 3),
                InitialBreed("Welsh Harlequin", 28, 3),
                InitialBreed("Call Duck", 28, 3),
                InitialBreed("Crested Duck", 28, 3),
                InitialBreed("Magpie", 28, 3),
                InitialBreed("Saxony", 28, 3),
                InitialBreed("Ancona", 28, 3)
            )
        ),
        InitialSpecies(
            name = "Quail",
            breeds = listOf(
                InitialBreed("Coturnix (Japanese)", 17, 2),
                InitialBreed("Bobwhite", 23, 2),
                InitialBreed("Button Quail", 16, 2)
            )
        ),
        InitialSpecies(
            name = "Goose",
            breeds = listOf(
                InitialBreed("Toulouse", 30, 3),
                InitialBreed("Embden", 30, 3),
                InitialBreed("African", 30, 3),
                InitialBreed("Chinese", 28, 3)
            )
        ),
        InitialSpecies(
            name = "Turkey",
            breeds = listOf(
                InitialBreed("Broad Breasted White", 28, 3),
                InitialBreed("Bronze", 28, 3),
                InitialBreed("Bourbon Red", 28, 3),
                InitialBreed("Narragansett", 28, 3)
            )
        )
    )
}
