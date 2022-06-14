package com.switcherette.boarribs.all_sightings_list

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.R
import com.switcherette.boarribs.all_sightings_list.AllSightingsListView.Event
import com.switcherette.boarribs.all_sightings_list.AllSightingsListView.ViewModel
import com.switcherette.boarribs.data.Sighting
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface AllSightingsListView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        data class LoadSightingDetails(val id: String) : Event()
    }

    sealed class ViewModel {
        object Loading : ViewModel()
        data class Content(val sightings: List<Sighting>) : ViewModel()
    }

    fun interface Factory : ViewFactory<AllSightingsListView>
}


class AllSightingsListViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    AllSightingsListView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val sightingsListRv: RecyclerView by lazy { findViewById(R.id.rv_show_list) }
    private val sightingsAdapter = SightingsAdapter {
        events.accept(Event.LoadSightingDetails(it.id))
    }

    init {
        sightingsListRv.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        sightingsListRv.adapter = sightingsAdapter
    }


    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_all_sightings_list
    ) : AllSightingsListView.Factory {
        override fun invoke(context: ViewFactory.Context): AllSightingsListView =
            AllSightingsListViewImpl(
                context.inflate(layoutRes)
            )
    }

    override fun accept(vm: ViewModel) {

    }


}