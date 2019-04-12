package com.bits.har.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bits.har.R;
import com.bits.har.services.FileWriterService;
import com.bits.har.services.SensorManagerService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment1 extends Fragment implements TextToSpeech.OnInitListener{
    private static final String TAG = "MyActivity";


    public static Intent serviceManagerIntent;
    public static Intent fileWriterServiceIntent;


    private static final String[] METADATA = {"Fast_Towards_Up",
            "Fast_Towards_Down",
            "Fast_Away_Up",
            "Fast_Away_Down",
            "Normal_Towards_Up",
            "Normal_Towards_Down",
            "Normal_Away_Up",
            "Normal_Away_Down",
            "Slow_Towards_Up",
            "Slow_Towards_Down",
            "Slow_Away_Up",
            "Slow_Away_Down"};
    private static final String[] labels = {"Fast", "Normal", "Slow"};
    public static String activityType = METADATA[5];

    OnFragmentInteractionListener callback;
    Intent intent;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TabFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment1 newInstance(String param1, String param2) {
        TabFragment1 fragment = new TabFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "2");
        args.putString(ARG_PARAM2, "3");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       /* intent = new Intent(this.getContext(), SensorManagerService.class);
        getActivity().startService(intent);

        Log.v(TAG, "Started Sensor Manager Service. ");*/
        Log.d(TAG,"Created TabFragment1");
        View rootView =  inflater.inflate(R.layout.fragment_tab_fragment1, container, false);

        Switch recordingSwitchtView = rootView.findViewById(R.id.record_data);

        recordingSwitchtView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getActivity(), "Recording Data!", Toast.LENGTH_SHORT)
                            .show();
                    if(fileWriterServiceIntent == null){
                        fileWriterServiceIntent = new Intent(getActivity(), FileWriterService.class);
                        Log.v(TAG, "Created File Writer Service. ");

                    }
                    getActivity().startService(fileWriterServiceIntent);
                    Log.v(TAG, "Started File Writer Service. ");

                    if(serviceManagerIntent == null){
                        serviceManagerIntent = new Intent(getActivity(), SensorManagerService.class);
                        Log.v(TAG, "Created Sensor Manager Service. ");

                    }
                    getActivity().startService(serviceManagerIntent);
                    Log.v(TAG, "Started Sensor");
                }else {
                    Toast.makeText(getActivity(), "Saving Data...", Toast.LENGTH_SHORT)
                            .show();
                    getActivity().stopService(fileWriterServiceIntent);
                    getActivity().stopService(serviceManagerIntent);
                }

            }
        });



        Spinner spinnerActivityType =  rootView.findViewById(R.id.spinner_activity_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,METADATA);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityType.setAdapter(adapter);
        spinnerActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                activityType = METADATA[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }

/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onInit(int i) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTabFragment1Interaction(int position);
    }

}
