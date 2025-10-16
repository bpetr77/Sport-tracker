package hu.bme.aut.android.sporttracker.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.sporttracker.data.model.BoundingBox
import hu.bme.aut.android.sporttracker.data.routePlanner.model.Graph
import hu.bme.aut.android.sporttracker.data.routePlanner.model.RouteSegment
import hu.bme.aut.android.sporttracker.data.routePlanner.repository.GraphRepository
import hu.bme.aut.android.sporttracker.data.routePlanner.repository.OSMRepository
import hu.bme.aut.android.sporttracker.domain.usecase.RoutePlannerUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoutePlannerViewModel(
    private val osmRepository: OSMRepository,
    private val graphRepository: GraphRepository,
    private val routePlannerUseCase: RoutePlannerUseCase = RoutePlannerUseCase()
) : ViewModel() {
    private val _routes = MutableStateFlow<List<RouteSegment>>(emptyList())
    val routes: StateFlow<List<RouteSegment>> = _routes

    private val _graph = MutableStateFlow<Graph?>(null)
    val graph: StateFlow<Graph?> = _graph

    init {
        viewModelScope.launch {
            //graphRepository.clearGraphData()
            val nodeCount = withContext(Dispatchers.IO) { graphRepository.countNodes() }

            if (nodeCount == 0) {
                val segments = withContext(Dispatchers.IO) { osmRepository.loadOsmData() }
                // Batch mentés memóriatakarékosan
                withContext(Dispatchers.IO) {
                    graphRepository.saveGraphFromSegments(segments)
                }
            }
        }
    }

    fun calculateBoundingBox(from: LatLng, to: LatLng): BoundingBox {
        val boundingBox = routePlannerUseCase.calculateBoundingBox(from, to)
        return boundingBox
    }

    fun loadGraphForArea(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) {
        viewModelScope.launch {
            val subGraph = graphRepository.loadSubGraph(minLat, maxLat, minLon, maxLon)
            _graph.value = subGraph
        }
    }
}