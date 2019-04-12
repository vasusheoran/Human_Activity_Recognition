package com.bits.har.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bits.har.R;
import com.bits.har.services.FileWriterService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<Path> pathList;
//    private View view;
    private  RecyclerView recyclerView;
    public static boolean isViewUpdated = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(!isViewUpdated && isVisibleToUser){
            isViewUpdated = true;
        }else if(isVisibleToUser){
            updateView();
        }

    }

    @SuppressWarnings("unused")
    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        isViewUpdated = false;
        return fragment;
    }
/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && pathList!=null){
            if(mAdapter!=null) {
                try {
                    pathList = FileWriterService.getFileList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }

    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            pathList = getUpdatedData();

            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(pathList, mListener));
            isViewUpdated = true;
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Path> getUpdatedData(){
        List<Path> paths = new ArrayList<>();
        try {
            paths = FileWriterService.getFileList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateView(){
        if(recyclerView!=null) {
            recyclerView.swapAdapter(new MyItemRecyclerViewAdapter(getUpdatedData(), mListener), true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Path item);
    }
}
