package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;
import ru.prokatvros.veloprokat.ui.adapters.InventoryAdapter;


public class SearchInventoryFragment extends BaseFragment {

    private static final String ARG_SEARCH_TYPE = "search_type";

    private static final int SEARCH_MAIN = 1;
    private static final int SEARCH_ADDITIONAL = 2;

    private int searchType = -1;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_search_base;
    }

    public static SearchInventoryFragment newSearchMain() {
        SearchInventoryFragment fragment = new SearchInventoryFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SEARCH_TYPE, SEARCH_MAIN);
        fragment.setArguments(args);

        return fragment;
    }

    public static SearchInventoryFragment newSearchAdditional() {
        SearchInventoryFragment fragment = new SearchInventoryFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SEARCH_TYPE, SEARCH_ADDITIONAL);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            searchType = getArguments().getInt(ARG_SEARCH_TYPE, -1);
        }

        if (searchType == -1) {
            throw new Error("SearchInventoryFragment was started without searchType, " +
                                "use one of method: newSearchMain(), newSearchAdditional() !!!");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_base, container, false);

        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getHostActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        final ListView lvInventory = (ListView) view.findViewById(R.id.lvList);
        EditText etInventoryName = (EditText) view.findViewById(R.id.etSearch);
        etInventoryName.setHint(getString(R.string.hint_enter_model_name));

        final List<Inventory> inventoryList = new ArrayList<>();
        final InventoryAdapter adapter = new InventoryAdapter(getHostActivity(), R.layout.item_base, inventoryList);
        lvInventory.setAdapter(adapter);
        lvInventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Rent rent = Rent.getRentFromPool(RentActivity.CREATE_RENT);

                if (searchType == SEARCH_MAIN) {
                    rent.inventory = adapter.getItem(position);
                } else if (searchType == SEARCH_ADDITIONAL) {
                    rent.inventoryAddition = adapter.getItem(position);
                }

                // Rent.getRentFromPool(RentActivity.CREATE_RENT).inventory = adapter.getItem(position);
                getHostActivity().onBackPressed();
            }
        });

        etInventoryName.setInputType(InputType.TYPE_CLASS_NUMBER);
        etInventoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                long idGroup = searchType == SEARCH_MAIN ? Inventory.MAIN_GROUP : Inventory.ADDITIONAL_GROUP;

                synchronized (adapter) {
                    adapter.clear();
                    if (!s.toString().isEmpty())
                        adapter.addAll(Inventory.getBySubNumber(s.toString(), idGroup));
                    adapter.notify();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rent, menu);
        menu.findItem(R.id.actionClose).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionClose:
                getHostActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
