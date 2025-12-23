package com.hatchbro.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SpeciesWithBreeds(
    @Embedded val species: SpeciesEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "speciesId"
    )
    val breeds: List<BreedEntity>
)
