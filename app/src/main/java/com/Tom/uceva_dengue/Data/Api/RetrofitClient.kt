import com.Tom.uceva_dengue.Data.Service.AuthService
import com.Tom.uceva_dengue.Data.Service.BloodTypeService
import com.Tom.uceva_dengue.Data.Service.CaseService
import com.Tom.uceva_dengue.Data.Service.CityService
import com.Tom.uceva_dengue.Data.Service.DengueService
import com.Tom.uceva_dengue.Data.Service.DepartmentService
import com.Tom.uceva_dengue.Data.Service.DiagnosticService
import com.Tom.uceva_dengue.Data.Service.GenreService
import com.Tom.uceva_dengue.Data.Service.HospitalService
import com.Tom.uceva_dengue.Data.Service.NotificationService
import com.Tom.uceva_dengue.Data.Service.PublicationService
import com.Tom.uceva_dengue.Data.Service.StatisticsService
import com.Tom.uceva_dengue.Data.Service.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLSocketFactory
import java.security.SecureRandom

object RetrofitClient {
    private const val BASE_URL = "https://api.prometeondev.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())  // Primero Scalars
            .addConverterFactory(GsonConverterFactory.create())  // Para respuestas en texto
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
    val departmentService: DepartmentService by lazy {
        retrofit.create(DepartmentService::class.java)
    }
    val genreService: GenreService by lazy {
        retrofit.create(GenreService::class.java)
    }
    val cityService: CityService by lazy {
        retrofit.create(CityService::class.java)
    }
    val bloodTypeService: BloodTypeService by lazy {
        retrofit.create(BloodTypeService::class.java)
    }
    val publicationService : PublicationService by lazy {
        retrofit.create(PublicationService::class.java)
    }
    val caseService : CaseService by lazy {
        retrofit.create(CaseService::class.java)
    }
    val userService : UserService by lazy {
        retrofit.create(UserService::class.java)
    }
    val dengueService : DengueService by lazy {
        retrofit.create(DengueService::class.java)
    }
    val hospitalService : HospitalService by lazy {
        retrofit.create(HospitalService::class.java)
    }
    val notificationService : NotificationService by lazy {
        retrofit.create(NotificationService::class.java)
    }
    val diagnosticService : DiagnosticService by lazy {
        retrofit.create(DiagnosticService::class.java)
    }
    val statisticsService : StatisticsService by lazy {
        retrofit.create(StatisticsService::class.java)
    }


}
