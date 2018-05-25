package com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.activities.historyListActivity.HistoryListActivity
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.HistoryRecyclerViewAdapter
import com.example.starkre.sleepAlertHistory.R

/**
 * @author Игорь Гулькин 25.04.2018.
 * <p>
 * Класс HistoryViewHolder является GUI объектом привязки
 * к конкретной истории при взаимодействии с пользователем.
 */


class HistoryViewHolder(itemView: View, private val parentAdapter: HistoryRecyclerViewAdapter)
    : RecyclerView.ViewHolder(itemView) {

    /**
     * 1.) parentAdapter - адаптер, который управляет объектами этого класса;
     * 2.) textViewHeadline - заголовок истории;
     * 3.) textViewDescription - описание истории;
     * 4.) historyRepositoryImageView - кнопка "Редактировать";
     * 5.) imageViewTick - картинка "Галочка";
     * 6.) imageViewTickOff - картинка "Пустая клетка";
     * 7.) currentPosition - позиция в списке историй.
     */

    var textViewHeadline: TextView? = null

    var textViewDescription: TextView? = null

    var historyRepositoryImageView: ImageView? = null

    var imageViewTick: ImageView? = null

    var imageViewTickOff: ImageView? = null

    var historyHeadlineTextEditor: EditText? = null

    var labelImageView: ImageView? = null

    internal var currentPosition: Int = 0

    private var relativeLayout: RelativeLayout? = null

    init {
        //Установка элементов:
        this.relativeLayout = itemView.findViewById(R.id.history_card_relative_layout)
        this.textViewHeadline = itemView.findViewById(R.id.history_card_headline)
        this.textViewDescription = itemView.findViewById(R.id.history_card_description)
        this.historyRepositoryImageView = itemView
                .findViewById(R.id.history_card_repository_image_view)
        this.imageViewTick = itemView.findViewById(R.id.history_card_tick_image_view)
        this.imageViewTickOff = itemView.findViewById(R.id.history_card_cell_image_view)
        this.historyHeadlineTextEditor = itemView.findViewById(R.id.history_card_headlne_edit_text)
        this.labelImageView = itemView.findViewById(R.id.history_card_label_image_view)
        //Установка обработчиков событий:
        this.installEventHandlers()
    }

    /**
     * installEventHandlers() устанавливает обработчки
     * событий на графические объекты.
     */

    private fun installEventHandlers() {
        this.itemView.setOnClickListener({
            this.parentAdapter.handleOnHistoryClick(this.imageViewTick!!, this.currentPosition)
            HistoryListActivity.THIS!!.refreshScreen()
        })
        this.itemView.setOnLongClickListener({
            HistoryListActivity.THIS!!.showOptionsView()
            HistoryListActivity.THIS!!.refreshScreen()
            true
        })
        this.historyRepositoryImageView?.setOnClickListener {
            this.parentAdapter.synchronize(this.currentPosition)
            HistoryListActivity.THIS!!.refreshScreen()
        }
        this.historyHeadlineTextEditor?.setOnEditorActionListener {
            _, actionID, _ ->
            this.parentAdapter.completeHistoryHeaderRenaming(actionID
                    , this.historyHeadlineTextEditor!!)
            HistoryListActivity.THIS!!.refreshScreen()
            true
        }
    }
}