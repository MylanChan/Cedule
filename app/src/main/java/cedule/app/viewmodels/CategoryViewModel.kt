package cedule.app.viewmodels

import androidx.lifecycle.ViewModel
import cedule.app.data.Database
import cedule.app.data.entities.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
}