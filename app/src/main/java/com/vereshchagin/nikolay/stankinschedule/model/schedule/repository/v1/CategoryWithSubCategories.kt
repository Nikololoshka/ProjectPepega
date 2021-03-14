package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import androidx.room.Embedded
import androidx.room.Relation

class CategoryWithSubCategories(
    @Embedded
    val category: CategoryEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent"
    )
    val subCategories: List<CategoryEntry>,
) {
    fun isFolder() = subCategories.isNotEmpty()
}
