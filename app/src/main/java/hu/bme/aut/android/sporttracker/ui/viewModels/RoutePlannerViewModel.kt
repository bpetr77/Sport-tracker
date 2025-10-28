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
import hu.bme.aut.android.sporttracker.domain.routePlanner.ShortestPath
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

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints

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
    fun clearPoints(){
        _routePoints.value = emptyList()
    }
    fun calculateBoundingBox(from: LatLng, to: LatLng): BoundingBox {
        val boundingBox = routePlannerUseCase.calculateBoundingBox(from, to)
        return boundingBox
    }

    suspend fun loadGraphForArea(boundingBox: BoundingBox) {
        val subGraph = graphRepository.loadSubGraph(boundingBox)
        _graph.value = subGraph
    }

    fun planRoute(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double){
        viewModelScope.launch(Dispatchers.Default) {
            val g = _graph.value ?: return@launch
            val sp = ShortestPath(g)

            val startId = sp.findNearestNodeId(fromLat, fromLon) ?: return@launch
            val goalId = sp.findNearestNodeId(toLat, toLon) ?: return@launch

            val pathIds = sp.findShortestPathIds(startId, goalId)
            val pathCoords = sp.idsToLatLon(pathIds)

            withContext(Dispatchers.Main) {
                _routePoints.value = pathCoords.map { LatLng(it.first, it.second) }
            }
        }
    }

    fun prepareAndPlanRoute(from: LatLng, to: LatLng){
        viewModelScope.launch {
            Log.d("RoutePlannerViewModel", "Preparing and planning route...")
            val boundingBox = calculateBoundingBox(from, to)
            Log.d("RoutePlannerViewModel", "Calculated bounding box: $boundingBox")
            loadGraphForArea(boundingBox)
            Log.d("RoutePlannerViewModel", "Graph loaded")
            planRoute(from.latitude, from.longitude, to.latitude, to.longitude)
            Log.d("RoutePlannerViewModel", "Route planned")

        }
    }
}