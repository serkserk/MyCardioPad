package com.mycardiopad.g1.mycardiopad.adapter.Adapter_ListDialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mycardiopad.g1.mycardiopad.R;

import java.util.List;


/**
 * Reprise de code
 * @author Created by Lewis on 30/08/2014.
 * @link https://github.com/lewisjdeane/L-Dialogs/blob/master/app/src/main/java/uk/me/lewisdeane/ldialogs/CustomListDialog.java
 */
public class CustomListDialog extends BaseDialog {

    // Context used to create the dialog.
    // Context utilisé pour créer le dialog
    private final Context mContext;

    // String containing the title text.
    // String contenant le titre
    private final String mTitle;

    // Array of string containing the items for the list.
    // Tableau de String contenant les items pour la list
    private List<Adapter_ListDialogs> modeList;

    private final String[] mItems;

    // Couleur du titre
    private final int mTitleColour;

    // Couleur des items
    static int mItemColour;

    // Taille du texte du titre
    private final int mTitleTextSize;

    // Taille du text des items
    static int mItemTextSize;

    // Alignement pour le titre
    private final Alignment mTitleAlignment;

    // Alignement pour les items
    static Alignment mItemAlignment;

    // TextView qui recevra le titre.
    private TextView mTitleView;

    // ListView qui reçevra le tableau d'item
    private ListView mListView;

    // Listener qui écoute les click sur les items
    private ListClickListener mCallbacks;

    static Typeface mTypeface;
    static Theme mTheme = Theme.LIGHT;

    /**
     * TODO: Add ability for multi choice mode.
     * TODO: Add radio buttons for single choice and checkboxes for multi choice.
     * TODO: Add ability to set selected item(s).
     * @author Lewis
     */

    // Make this class private so it can only be built through the builder.
    private CustomListDialog(Builder _builder) {

        // Appel du constructeur de la classe parente pour créer le dialog
        super(new ContextThemeWrapper(_builder.mContext, _builder.mIsDark ? R.style.LDialogs_Dark : R.style.LDialogs_Light));

        // Mise en place des propriétés
        this.mContext = _builder.mContext;
        mTheme = _builder.mIsDark ? Theme.DARK : Theme.LIGHT;
        this.mTitle = _builder.mTitle;
        this.mItems = _builder.mItems;
        this.modeList = _builder.modeList;
        this.mTitleColour = _builder.mTitleColour != 0 ? _builder.mTitleColour : (mTheme == Theme.DARK ? Color.parseColor(DarkColours.TITLE.mColour) : Color.parseColor(LightColours.TITLE.mColour));
        mItemColour = _builder.mItemColour != 0 ? _builder.mItemColour : (mTheme == Theme.DARK ? Color.parseColor(DarkColours.ITEM.mColour) : Color.parseColor(LightColours.ITEM.mColour));
        this.mTitleAlignment = _builder.mTitleAlignment;
        mItemAlignment = _builder.mItemAlignment;
        this.mTitleTextSize = _builder.mTitleTextSize;
        mItemTextSize = _builder.mItemTextSize;
        mTypeface = _builder.mTypeface == null ? Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Medium.ttf") : _builder.mTypeface;

        // Mise en place du dialog
        init();

        // Mise en place des listeners
        setListeners();

        // Mise en place des propriétés du titre.
        setTitleProperties();
    }

    private void init() {
        // Référence vers la vue racine
        @SuppressLint("InflateParams") View mRootView = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_list_custom,
                null);

        // Référence des view nécessaire au dialog
        mTitleView = (TextView) mRootView.findViewById(R.id.dialog_list_custom_title);
        mListView = (ListView) mRootView.findViewById(R.id.dialog_list_custom_list);


        // Référence de l'adaptateur utilisé dans notre list
        CustomListViewAdapter mCustomListViewAdapter = new CustomListViewAdapter(mContext, modeList);
        mListView.setAdapter(mCustomListViewAdapter);
        // Mise en place vers la vue racine
        super.setView(mRootView);
    }

    private void setListeners() {
        // Mise en place des items dans la vue
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // If there is a listener available call onListItemSelected.
                if (mCallbacks != null)
                    mCallbacks.onListItemSelected(i, mItems, mItems[i]);

                dismiss();
            }
        });
    }

    private CustomListDialog setTitleProperties() {
        // Application des propriétés sur le titre si c'est valable
        if (mTitleView != null) {
            mTitleView.setText(this.mTitle);
            mTitleView.setTextColor(this.mTitleColour);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize);
            mTitleView.setTypeface(mTypeface);
            mTitleView.setGravity(getGravityFromAlignment(this.mTitleAlignment) | Gravity.CENTER_VERTICAL);
        }
        return this;
    }

    public ListView getListView() {
        return mListView;
    }   //retourne la listView

    public CustomListDialog setListClickListener(ListClickListener mCallbacks) {
        // Mise en place du clickListener
        this.mCallbacks = mCallbacks;
        return this;
    }

    public interface ListClickListener {
        void onListItemSelected(int position, String[] items, String item);
    }

    public static class Builder {

        // Champs requis par le constructeur
        private final Context mContext;
        private final String mTitle;
        private final String[] mItems;
        private List<Adapter_ListDialogs> modeList;

        // Le Builder utilisé pour générer la list
        public Builder(Context _context, String _title, String[] _items, List<Adapter_ListDialogs> modeList) {
            this.mContext = _context;
            this.mTitle = _title;
            this.mItems = _items;
            this.modeList = modeList;
        }

        // Les paramètres optionnels
        private Alignment mTitleAlignment;
        private Alignment mItemAlignment;
        private int mTitleColour, mItemColour, mTitleTextSize, mItemTextSize;
        private boolean mIsDark = false;
        private Typeface mTypeface;

        public Builder typeface(Typeface _typeface){
            this.mTypeface = _typeface;
            return this;
        }

        public Builder titleAlignment(Alignment _alignment) {
            this.mTitleAlignment = _alignment;
            return this;
        }

        public Builder itemAlignment(Alignment _alignment) {
            this.mItemAlignment = _alignment;
            return this;
        }

        public Builder titleColor(String _colour) {
            this.mTitleColour = Color.parseColor(_colour);
            return this;
        }

        public Builder itemColor(int _colour) {
            this.mItemColour = _colour;
            return this;
        }

        public Builder darkTheme(boolean _isDark) {
            this.mIsDark = _isDark;
            return this;
        }

        public  Builder titleTextSize(int _textSize){
            this.mTitleTextSize = _textSize;
            return this;
        }

        public Builder itemTextSize(int _textSize){
            this.mItemTextSize = _textSize;
            return this;
        }

        public Builder rightToLeft(boolean _rightToLeft){
            if(_rightToLeft){
                this.mTitleAlignment = Alignment.RIGHT;
                this.mItemAlignment = Alignment.RIGHT;
            }
            return this;
        }

        public CustomListDialog build() {
            return new CustomListDialog(this);
        }


    }

}