package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.FragmentRecipeStepBinding;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.content.res.Configuration.ORIENTATION_SQUARE;
import static com.example.android.bakingapp.model.Recipe.Step;

public class RecipeStepFragment extends Fragment {

    private SimpleExoPlayer mExoPlayer;
    private FragmentRecipeStepBinding mBinding;
    private ArrayList<Step> steps;
    private String recipeName;
    private String videoURL;
    private int pos;
    private int lastPosition;
    boolean isTablet;

    public RecipeStepFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If true, we're in tablet mode.
        if (getArguments() != null) {
            steps = getArguments()
                    .getParcelableArrayList("steps");
            recipeName = getArguments()
                    .getString("recipeName");
            pos = getArguments()
                    .getInt("position");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_recipe_step, container, false);
        View rootView = mBinding.getRoot();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (steps == null) {
            Intent intent = getActivity().getIntent();
            steps = intent.getParcelableArrayListExtra("steps");
            recipeName = intent.getStringExtra("recipeName");
            pos = intent.getIntExtra("position", 0);
        }

        getActivity().setTitle(recipeName);
        Step currentStep = steps.get(pos);
        lastPosition = steps.size() - 1;
        isTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getScreenOrientation();
        videoURL = currentStep.getVideoURL();

        //Adjust layout based on content
        adjustLayout(orientation);

        if (videoURL.isEmpty()) {
            mBinding.exoplayerView.setVisibility(View.GONE);
            mBinding.novideoMessageTv.setVisibility(View.VISIBLE);
            mBinding.novideoIcon.setVisibility(View.VISIBLE);
        } else {
            initializePlayer(Uri.parse(videoURL));
        }
        mBinding.stepDescriptionTv.setText(currentStep.getDescription());

        // Icon made by Lyolya from www.flaticon.com

        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.exo_next_step);
        ImageButton prevButton = (ImageButton) rootView.findViewById(R.id.exo_prev);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos != 0) {
                    moveToPreviousStep();
                } else {
                    Toast.makeText(getActivity(), "This is the first step",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos != lastPosition) {
                    moveToNextStep();
                } else {
                    Toast.makeText(getActivity(), "This is the last step",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBinding.previousStepArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos != 0) {
                    moveToPreviousStep();
                }
            }
        });
        mBinding.nextStepArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos != lastPosition) {
                    moveToNextStep();
                }
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    private void standardButtonsView() {
        mBinding.previousStepArrow.setVisibility(View.VISIBLE);
        mBinding.previousStepLabel.setVisibility(View.VISIBLE);
        mBinding.nextStepArrow.setVisibility(View.VISIBLE);
        mBinding.nextStepLabel.setVisibility(View.VISIBLE);
    }

    private void backButtonDisabledView() {
        mBinding.nextStepArrow.setVisibility(View.VISIBLE);
        mBinding.nextStepLabel.setVisibility(View.VISIBLE);
    }

    private void nextButtonDisabledView() {
        mBinding.previousStepLabel.setVisibility(View.VISIBLE);
        mBinding.previousStepArrow.setVisibility(View.VISIBLE);
    }

    private void moveToPreviousStep() {
        if (!isTablet) {
            Intent previousStep = new Intent(getActivity(), RecipeStepActivity.class);
            previousStep.putParcelableArrayListExtra("steps", steps);
            previousStep.putExtra("recipeName", recipeName);
            previousStep.putExtra("position", pos - 1);
            if (mExoPlayer != null) {
                releasePlayer();
            }
            startActivity(previousStep);
        } else {
            ((DetailActivity) getActivity()).replaceStepFragment(steps,
                    recipeName, pos - 1);
        }
    }

    private void moveToNextStep() {
        if (!isTablet) {
            Intent nextStep = new Intent(getActivity(), RecipeStepActivity.class);
            nextStep.putParcelableArrayListExtra("steps", steps);
            nextStep.putExtra("recipeName", recipeName);
            nextStep.putExtra("position", pos + 1);
            if (mExoPlayer != null) {
                releasePlayer();
            }
            startActivity(nextStep);
        } else {
            ((DetailActivity) getActivity()).replaceStepFragment(steps,
                    recipeName, pos + 1);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mBinding.exoplayerView.setPlayer(mExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void adjustLayout(int orientation) {
        //Landscape layout settings for recipe step with a video in a phone (landscape)
        if (orientation == ORIENTATION_LANDSCAPE && !videoURL.isEmpty() && !isTablet) {
            mBinding.stepDescriptionLayout.setVisibility(View.GONE);
            mBinding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 0);
            mBinding.playerLayout.setLayoutParams(params);

            //taken from: https://stackoverflow.com/questions/38254127/programmatically-enable-disable-immersive-mode
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        // Layout settings for landscape layout with a video in a tablet
        if (orientation == ORIENTATION_LANDSCAPE && !videoURL.isEmpty() && isTablet) {
            RelativeLayout.LayoutParams paramsPlayer = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            paramsPlayer.setMargins(0, 24, 16, 0);
            mBinding.exoplayerView.setLayoutParams(paramsPlayer);

            LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 2
            );
            LinearLayout.LayoutParams descriptionLayout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 4
            );
            mBinding.playerLayout.setLayoutParams(paramsLayout);
            mBinding.stepDescriptionLayout.setLayoutParams(descriptionLayout);
        }
        // Layout settings for portrait layout or landscape if there's no video.
        if (orientation == ORIENTATION_PORTRAIT ||
                (orientation == ORIENTATION_LANDSCAPE && videoURL.isEmpty())) {
            if (pos == 0) {
                backButtonDisabledView();
            } else if (pos > 0 && pos < lastPosition) {
                standardButtonsView();
            } else if (pos == lastPosition) {
                nextButtonDisabledView();
            }
        }
        // Landscape layout settings if there's no video.
        if (orientation == ORIENTATION_LANDSCAPE && videoURL.isEmpty()) {
            mBinding.descriptionCard.setVisibility(View.VISIBLE);
            mBinding.playerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 2));
            mBinding.stepDescriptionLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1));
            standardButtonsView();
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            releasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    public int getScreenOrientation() {
        Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
        int orientation;
        if (getOrient.getWidth() == getOrient.getHeight()) {
            orientation = ORIENTATION_SQUARE;
        } else {
            if (getOrient.getWidth() < getOrient.getHeight()) {
                orientation = ORIENTATION_PORTRAIT;
            } else {
                orientation = ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }
}