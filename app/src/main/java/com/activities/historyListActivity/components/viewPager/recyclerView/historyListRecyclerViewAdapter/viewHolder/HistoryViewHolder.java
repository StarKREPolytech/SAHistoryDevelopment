package com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.starkre.sleepAlertHistory.R;
import com.activities.historyListActivity.HistoryListActivity;
import com.activities.historyListActivity.components.viewPager
        .recyclerView.historyListRecyclerViewAdapter.HistoryRecyclerViewAdapter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Игорь Гулькин 25.04.2018.
 * <p>
 * Класс HistoryViewHolder является GUI объектом привязки
 * к конкретной истории при взаимодействии с пользователем.
 */

@Slf4j
public final class HistoryViewHolder extends RecyclerView.ViewHolder {

    /**
     * 1.) parentAdapter - адаптер, который управляет объектами этого класса;
     * 2.) textViewHeadline - заголовок истории;
     * 3.) textViewDescription - описание истории;
     * 4.) historyRepositoryButton - кнопка "Редактировать";
     * 5.) imageViewTick - картинка "Галочка";
     * 6.) imageViewTickOff - картинка "Пустая клетка";
     * 7.) currentPosition - позиция в списке историй.
     */

    private final HistoryRecyclerViewAdapter parentAdapter;

    @Getter
    private RelativeLayout relativeLayout;

    @Getter
    private LinearLayout linearLayoutText;

    @Getter
    private TextView textViewHeadline;

    @Getter
    private TextView textViewDescription;

    @Getter
    private ImageButton historyRepositoryButton;

    @Getter
    private ImageView imageViewTick;

    @Getter
    private ImageView imageViewTickOff;

    @Getter
    private EditText historyHeadlineTextEditor;

    @Getter
    private ImageView labelImageView;

    @Getter
    @Setter
    private int currentPosition;

    public HistoryViewHolder(final View itemView, final HistoryRecyclerViewAdapter parentAdapter) {
        super(itemView);
        this.parentAdapter = parentAdapter;
        //Установка элементов:
        this.relativeLayout = itemView.findViewById(R.id.history_item_relative_layout);
        this.textViewHeadline = itemView.findViewById(R.id.textViewHead);
        this.textViewDescription = itemView.findViewById(R.id.textViewDescription);
        this.historyRepositoryButton = itemView.findViewById(R.id.history_repository_button);
        this.imageViewTick = itemView.findViewById(R.id.history_tick);
        this.imageViewTickOff = itemView.findViewById(R.id.history_tick_off);
        this.historyHeadlineTextEditor = itemView.findViewById(R.id.history_edit_head);
        this.linearLayoutText = itemView.findViewById(R.id.history_linear_layout_text);
        this.labelImageView = itemView.findViewById(R.id.history_label_image_view);
        //Установка обработчиков событий:
        this.installEventHandlers();
    }

    /**
     * installEventHandlers() устанавливает обработчки
     * событий на графические объекты.
     */

    private void installEventHandlers() {
        this.itemView.setOnClickListener(view -> {
            this.parentAdapter.handleOnHistoryClick(this.imageViewTick, this.currentPosition);
            HistoryListActivity.THIS.refreshScreen();
        });
        this.itemView.setOnLongClickListener(view -> {
            this.parentAdapter.changeMode(this.imageViewTick, this.currentPosition);
            HistoryListActivity.THIS.refreshScreen();
            return true;
        });
        this.historyRepositoryButton.setOnClickListener(view -> {
            this.parentAdapter.synchronize(this.currentPosition);
            HistoryListActivity.THIS.refreshScreen();
        });
        this.historyHeadlineTextEditor.setOnEditorActionListener((textView, actionID, keyEvent) -> {
            this.parentAdapter.completeHistoryHeaderRenaming(actionID, this.historyHeadlineTextEditor);
            HistoryListActivity.THIS.refreshScreen();
            return true;
        });
    }
}