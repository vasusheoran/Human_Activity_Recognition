package com.bits.har.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bits.har.R;
import com.bits.har.main.MainTabActivity;
import com.bits.har.metadata.Constants;
import com.bits.har.services.FileWriterService;
import com.bits.har.services.SensorManagerService;

import java.util.Timer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TabFragmentDataCollection extends Fragment implements TextToSpeech.OnInitListener{
    private static final String TAG = "FragmentDataCollection";


    public static Intent serviceManagerIntent;
    public static Intent fileWriterServiceIntent;

//    public static boolean isVoiceEnabled;



    public static String orientationType = Constants.ORIENTATION[0];
    public static String activityType = Constants.ACTIVITY_TYPE[0];

    private OnFragmentInteractionListener mListener;

    public TabFragmentDataCollection() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"Created TabFragmentDataCollection");
        View rootView =  inflater.inflate(R.layout.fragment_tab_fragment1, container, false);

        Switch recordingSwitchtView = rootView.findViewById(R.id.record_data);

        Switch enable_voice_switch = rootView.findViewById(R.id.switch_enable_voice);

        enable_voice_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // If the switch button is on

                    Toast.makeText(getActivity(), "Voice Enabled", Toast.LENGTH_SHORT)
                            .show();


                    MainTabActivity.isVoiceEnabled = true;
                    new Timer().scheduleAtFixedRate(new MainTabActivity.updateActivity(), 1000, 3000);
                    new Timer().scheduleAtFixedRate(new MainTabActivity.CalculateProbabilty(), 1000, 2000);

                }
                else {
                    // If the switch button is off

                    Toast.makeText(getActivity(), "Voice Disabled", Toast.LENGTH_SHORT)
                            .show();

                    MainTabActivity.isVoiceEnabled = false;

                }
            }
        });




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



        Spinner spinnerOrientationType =  rootView.findViewById(R.id.spinner_orientation_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, Constants.ORIENTATION);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrientationType.setAdapter(adapter);
        spinnerOrientationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                orientationType = Constants.ORIENTATION[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinnerActivityType =  rootView.findViewById(R.id.spinner_activity_type);
        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, Constants.ACTIVITY_TYPE);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityType.setAdapter(adapter);
        spinnerActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                activityType = Constants.ACTIVITY_TYPE[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        return rootView;
    }

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
