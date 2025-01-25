package cedule.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cedule.app.data.Database
import cedule.app.data.entities.Category
import cedule.app.utils.AlarmUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val db: Database) : ViewModel() {
    fun getCategory(id: Int): Flow<Category?> {
        return db.categoryDAO().getById(id)
    }

    fun getSimilar(name: String): Flow<List<Category>> {
        return db.categoryDAO().getSimilar(name)
    }

    fun getByName(name: String): Flow<Category?> {
        return db.categoryDAO().getByName(name)
    }

    fun saveCategory(category: String, color: Long) {
        viewModelScope.launch {
            db.categoryDAO().insertOrUpdate(category, color.toInt())
        }
    }
}