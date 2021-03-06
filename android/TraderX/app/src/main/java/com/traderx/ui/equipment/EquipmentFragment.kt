package com.traderx.ui.equipment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.traderx.R
import com.traderx.api.ErrorHandler
import com.traderx.api.response.CommentResponse
import com.traderx.api.response.EquipmentResponse
import com.traderx.type.PredictionType
import com.traderx.ui.comment.CommentFragment
import com.traderx.util.FragmentTitleEmitters
import com.traderx.util.Helper
import com.traderx.util.Injection
import com.traderx.viewmodel.EquipmentViewModel
import io.reactivex.disposables.CompositeDisposable
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.view.LineChartView
import retrofit2.HttpException

class EquipmentFragment : Fragment(), FragmentTitleEmitters {
    private var equipmentCode: String? = null
    private lateinit var equipmentViewModel: EquipmentViewModel

    private lateinit var code: TextView
    private lateinit var name: TextView
    private lateinit var value: TextView
    private lateinit var percentage: TextView
    private lateinit var prediction: TextView
    private lateinit var equipmentType: TextView
    private lateinit var stock: TextView
    private lateinit var chart: LineChartView
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            equipmentCode = it.getString(ARG_EQUIPMENT_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setFragmentTitle(context, equipmentCode)

        val equipmentViewModelFactory =
            Injection.provideEquipmentViewModelFactory(context as Context)

        equipmentViewModel = ViewModelProvider(this, equipmentViewModelFactory)
            .get(EquipmentViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_equipment, container, false)

        code = root.findViewById(R.id.code)
        name = root.findViewById(R.id.name)
        value = root.findViewById(R.id.value)
        percentage = root.findViewById(R.id.percentage)
        stock = root.findViewById(R.id.stock)
        prediction = root.findViewById(R.id.prediction)
        equipmentType = root.findViewById(R.id.equipment_type)

        root.findViewById<Button>(R.id.buy_action)?.let {
            it.setOnClickListener {
                findNavController().navigate(
                    EquipmentFragmentDirections.actionNavigationEquipmentToNavigationTransaction(
                        equipmentCode ?: ""
                    )
                )
            }
        }

        root.findViewById<Button>(R.id.alert_action)?.let {
            it.setOnClickListener {
                findNavController().navigate(
                    EquipmentFragmentDirections.actionNavigationEquipmentToNavigationAlert(
                        equipmentCode ?: ""
                    )
                )
            }
        }

        root.findViewById<ImageView>(R.id.increase_prediction_action)?.let { view ->
            view.setOnClickListener {
                makePrediction(PredictionType.INCREASE)
            }
        }

        root.findViewById<ImageView>(R.id.decrease_prediction_action)?.let { view ->
            view.setOnClickListener {
                makePrediction(PredictionType.DECREASE)
            }
        }

        root.findViewById<ImageView>(R.id.stable_prediction_action)?.let { view ->
            view.setOnClickListener {
                makePrediction(PredictionType.STABLE)
            }
        }

        root.findViewById<FrameLayout>(R.id.comments)?.let {
            val commentFragment = CommentFragment(
                equipmentViewModel.getComments(equipmentCode ?: "")
                    .compose(Helper.applySingleSchedulers<ArrayList<CommentResponse>>()),
                { id ->
                    equipmentViewModel.deleteComment(id)
                },
                { id, message ->
                    equipmentViewModel.editComment(id, message)
                },
                { message ->
                    equipmentViewModel.createComment(equipmentCode ?: "", message)
                },
                { id, vote ->
                    equipmentViewModel.voteComment(id, vote)
                },
                { id ->
                    equipmentViewModel.revokeComment(id)
                }
            )

            val fragmentTransaction = fragmentManager?.beginTransaction()

            fragmentTransaction?.add(it.id, commentFragment, CommentFragment.TAG)
            fragmentTransaction?.commit()
        }

        chart = root.findViewById(R.id.chart)
        chart.setInteractive(false)

        disposable.add(
            equipmentViewModel.getEquipment(equipmentCode ?: "")
                .compose(Helper.applySingleSchedulers<EquipmentResponse>())
                .subscribe({
                    updateView(it)
                }, {
                    ErrorHandler.handleError(it, context as Context)
                })
        )

        return root
    }

    private fun updateView(equipment: EquipmentResponse) {
        code.text = equipment.equipment.code
        name.text = equipment.equipment.name
        value.text = String.format("%,.2f", equipment.equipment.currentValue)
        prediction.text = String.format("%,.2f", equipment.equipment.predictionRate)
        equipmentType.text = equipment.equipment.localizedType(context as Context)
        stock.text = String.format("%,.2f", equipment.equipment.currentStock)

        updateChart(chart, equipment.historicalValues)
    }

    private fun updateChart(chart: LineChartView, history: List<EquipmentResponse.History>) {
        val pointOpens = ArrayList<PointValue>()
        val pointCloses = ArrayList<PointValue>()
        val lines = ArrayList<Line>()
        val data = LineChartData()

        val size = history.size
        var max = 0f
        var min = if (size > 0) history[0].open.toFloat() else 0f
        // Take last 30 points
        for (i in Math.max(0, size - 30) until size) {
            pointOpens.add(PointValue(i.toFloat(), history[i].open.toFloat()))
            pointCloses.add(PointValue(i.toFloat(), history[i].close.toFloat()))
            max = Math.max(max, history[i].open.toFloat())
            min = Math.min(min, history[i].open.toFloat())
        }

//        val lineOpen = Line(pointOpens).setColor(0xdd0000)
//        lineOpen.pointRadius = 2
//        lineOpen.strokeWidth = 2
//        lineOpen.pointColor = Color.argb(150, 20, 220, 20)
//
        val lineClose = Line(pointCloses).setColor(0xdd0000).setFilled(true)
        lineClose.pointRadius = 2
        lineClose.pointColor = Color.argb(150, 220, 20, 20)
//        lines.add(lineOpen)
        lines.add(lineClose)

        data.setLines(lines).setBaseValue(0f).setAxisYLeft(Axis())

        chart.setLineChartData(data)

        val v = chart.maximumViewport
        v.set(v.left, max * (1.01f), v.right, min / (1.02f))
        chart.maximumViewport = v
        chart.currentViewport = v
    }

    private fun makePrediction(type: PredictionType) {
        disposable.add(
            equipmentViewModel.createPrediction(equipmentCode ?: "", type)
                .compose(Helper.applyCompletableSchedulers())
                .subscribe({
                    Snackbar.make(
                        requireView(),
                        getString(R.string.make_prediction_success),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }, {
                    if (it is HttpException && it.code() == 406) {
                        Snackbar.make(
                            requireView(),
                            getString(R.string.make_prediction_already_error),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        ErrorHandler.handleError(it, context as Context)
                    }
                })
        )
    }

    companion object {
        private const val ARG_EQUIPMENT_NAME = "equipment_name"
    }
}