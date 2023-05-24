import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.example.lab4.R


class Fragment3 : Fragment() {
    var btn1: Button? = null
    var btn2: Button? = null
    var btn3: Button? = null
    var btn4: Button? = null
    var btn5: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.page3, null)
        return view
    }
}