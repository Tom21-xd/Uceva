
import com.Tom.uceva_dengue.Domain.Entities.Departamento
import kotlinx.coroutines.flow.Flow

interface IDepartamentoRepository {
    fun getDepartamentos(): Flow<List<Departamento>>
}
