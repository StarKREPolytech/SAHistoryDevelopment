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
     * 2.) headlineTextView - заголовок истории;
     * 3.) descriptionTextView - описание истории;
     * 4.) historyRepositoryImageView - кнопка "Редактировать";
     * 5.) tickImageView - картинка "Галочка";
     * 6.) tickOffImageView - картинка "Пустая клетка";
     * 7.) historyHeadlineTextEditor - строка переименовывания;
     * 8.) labelImageView - логотип истории;
     * 9.) cardRelativeLayout - вся область истории;
     * 10.) currentPosition - позиция в списке историй.
     */

    var headlineTextView: TextView? = null

    var descriptionTextView: TextView? = null

    var historyRepositoryImageView: ImageView? = null

    var tickImageView: ImageView? = null

    var tickOffImageView: ImageView? = null

    var historyHeadlineTextEditor: EditText? = null

    var labelImageView: ImageView? = null

    internal var currentPosition: Int = 0

    private var cardRelativeLayout: RelativeLayout? = null

    init {
        //Установка элементов:
        this.cardRelativeLayout = itemView.findViewById(R.id.history_card_relative_layout)
        this.headlineTextView = itemView.findViewById(R.id.history_card_headline)
        this.descriptionTextView = itemView.findViewById(R.id.history_card_description)
        this.historyRepositoryImageView = itemView
                .findViewById(R.id.history_card_repository_image_view)
        this.tickImageView = itemView.findViewById(R.id.history_card_tick_image_view)
        this.tickOffImageView = itemView.findViewById(R.id.history_card_cell_image_view)
        this.historyHeadlineTextEditor = itemView.findViewById(R.id.history_card_headline_edit_text)
        this.labelImageView = itemView.findViewById(R.id.history_card_label_image_view)
        //Установка обработчиков событий:
        this.installEventHandlers()
    }

    /**
     * installEventHandlers() устанавливает обработчки
     * событий на графические объекты.
     */

    private fun installEventHandlers() {
        this.cardRelativeLayout?.setOnClickListener({
            this.parentAdapter.handleOnHistoryClick(this.tickImageView!!, this.currentPosition)
            HistoryListActivity.THIS!!.refresh()
        })
        this.cardRelativeLayout?.setOnLongClickListener({
            HistoryListActivity.THIS!!.setEditMode()
            HistoryListActivity.THIS!!.refresh()
            true
        })
        this.historyHeadlineTextEditor?.setOnEditorActionListener { _, actionID, _
            -> this.parentAdapter.completeHistoryHeaderRenaming(actionID
                , this.historyHeadlineTextEditor!!)
            HistoryListActivity.THIS!!.refresh()
            true
        }
    }
}