package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import com.google.android.exoplayer2.C;
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
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.content.res.Configuration.ORIENTATION_SQUARE;
import static com.example.android.bakingapp.model.Recipe.Step;
import static com.example.android.bakingapp.ui.DetailFragment.POSITION_KEY;
import static com.example.android.bakingapp.ui.DetailFragment.RECIPE_NAME_KEY;
import static com.example.android.bakingapp.ui.DetailFragment.STEPS_KEY;

public class RecipeStepFragment extends Fragment {

    private static final String PLAYER_POSITION_KEY = "positionKEY";
    private static final String STATE_KEY = "state";
    private static final String STEP_POSITION_KEY = "recipePositionKey";
    private SimpleExoPlayer mExoPlayer;
    private FragmentRecipeStepBinding mBinding;
    private List<Step> mSteps = new ArrayList<>();
    private String mRecipeName;
    private String mVideoURL;
    private int mPos;
    private int mLastPosition;
    boolean mIsTablet;
    private Long mPlayerPosition;
    private boolean mPlaybackState;

    public RecipeStepFragment() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mExoPlayer != null){
            outState.putLong(PLAYER_POSITION_KEY, mPlayerPosition);
            outState.putBoolean(STATE_KEY, mPlaybackState);
        }
        outState.putParcelableArrayList(STEPS_KEY, (ArrayList<? extends Parcelable>) mSteps);
        outState.putString(RECIPE_NAME_KEY, mRecipeName);
        outState.putInt(STEP_POSITION_KEY, mPos);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If true, we're in tablet mode.
        if (getArguments() != null) {
            mSteps = getArguments()
                    .getParcelableArrayList(STEPS_KEY);
            mRecipeName = getArguments()
                    .getString(RECIPE_NAME_KEY);
            mPos = getArguments()
                    .getInt(POSITION_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.getLong(PLAYER_POSITION_KEY) != 0) {
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION_KEY);
            mPlaybackState = savedInstanceState.getBoolean(STATE_KEY);
            mSteps = savedInstanceState.getParcelableArrayList(STEPS_KEY);
            mRecipeName = savedInstanceState.getString(RECIPE_NAME_KEY);
            mPos = savedInstanceState.getInt(STEP_POSITION_KEY);
        } else {
            mPlayerPosition = C.TIME_UNSET;
            mPlaybackState = true;
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

        if (mSteps == null) {
            Intent intent = getActivity().getIntent();
            mSteps = intent.getParcelableArrayListExtra(STEPS_KEY);
            mRecipeName = intent.getStringExtra(RECIPE_NAME_KEY);
            mPos = intent.getIntExtra(POSITION_KEY, 0);
        }

        getActivity().setTitle(mRecipeName);
        Step currentStep = mSteps.get(mPos);
        mLastPosition = mSteps.size() - 1;
        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getScreenOrientation();
        mVideoURL = currentStep.getVideoURL();

        //Adjust layout based on content
        adjustLayout(orientation);

        if (mVideoURL.isEmpty()) {
            mBinding.exoplayerView.setVisibility(View.GONE);
            mBinding.novideoMessageTv.setVisibility(View.VISIBLE);
            mBinding.novideoIcon.setVisibility(View.VISIBLE);
        } else {
            initializePlayer(Uri.parse(mVideoURL));
        }
        mBinding.stepDescriptionTv.setText(currentStep.getDescription());

        // Icon made by Lyolya from www.flaticon.com
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.exo_next_step);
        ImageButton prevButton = (ImageButton) rootView.findViewById(R.id.exo_prev);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPos != 0) {
                    moveToPreviousStep();
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.first_step_message),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPos != mLastPosition) {
                    moveToNextStep();
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.last_step_message),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBinding.previousStepArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPos != 0) {
                    moveToPreviousStep();
                }
            }
        });
        mBinding.nextStepArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPos != mLastPosition) {
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
        if (!mIsTablet) {
            Intent previousStep = new Intent(getActivity(), RecipeStepActivity.class);

            previousStep.putParcelableArrayListExtra(STEPS_KEY, (ArrayList<? extends Parcelable>) mSteps);
            previousStep.putExtra(RECIPE_NAME_KEY, mRecipeName);
            previousStep.putExtra(POSITION_KEY, mPos - 1);
            if (mExoPlayer != null) {
                releasePlayer();
            }
            startActivity(previousStep);
        } else {
            ((DetailActivity) getActivity()).replaceStepFragment((ArrayList<Step>) mSteps,
                    mRecipeName, mPos - 1);
        }
    }

    private void moveToNextStep() {
        if (!mIsTablet) {
            Intent nextStep = new Intent(getActivity(), RecipeStepActivity.class);
            nextStep.putParcelableArrayListExtra(STEPS_KEY, (ArrayList<? extends Parcelable>) mSteps);
            nextStep.putExtra(RECIPE_NAME_KEY, mRecipeName);
            nextStep.putExtra(POSITION_KEY, mPos + 1);
            if (mExoPlayer != null) {
                releasePlayer();
            }
            startActivity(nextStep);
        } else {
            ((DetailActivity) getActivity()).replaceStepFragment((ArrayList<Step>) mSteps,
                    mRecipeName, mPos + 1);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mBinding.exoplayerView.setPlayer(mExoPlayer);

            // Taken from https://stackoverflow.com/questions/45481775/
            if (mPlayerPosition != C.TIME_UNSET) {
                mExoPlayer.seekTo(mPlayerPosition);
            }
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), getActivity().getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(mPlaybackState);
        }
    }

    private void adjustLayout(int orientation) {
        //Landscape layout settings for recipe step with a video in a phone (landscape)
        setLandscapeLayoutPhoneWithVideo(orientation);
        // Layout settings for landscape layout with a video in a tablet
        setTabletLayoutWithVideo(orientation);
        // Layout settings for portrait layout or landscape if there's no video.
        setLayoutIfNoVideo(orientation);
        // Landscape layout settings if there's no video.
        setLandscapeLayoutIfNoVideo(orientation);
    }

    private void setLandscapeLayoutPhoneWithVideo(int orientation) {
        if (orientation == ORIENTATION_LANDSCAPE && !mVideoURL.isEmpty() && !mIsTablet) {
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
    }

    private void setTabletLayoutWithVideo(int orientation) {
        if (orientation == ORIENTATION_LANDSCAPE && !mVideoURL.isEmpty() && mIsTablet) {
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
    }

    private void setLayoutIfNoVideo(int orientation) {
        if (orientation == ORIENTATION_PORTRAIT ||
                (orientation == ORIENTATION_LANDSCAPE && mVideoURL.isEmpty())) {
            if (mPos == 0) {
                backButtonDisabledView();
            } else if (mPos > 0 && mPos < mLastPosition) {
                standardButtonsView();
            } else if (mPos == mLastPosition) {
                nextButtonDisabledView();
            }
        }
    }

    private void setLandscapeLayoutIfNoVideo(int orientation) {
        if (orientation == ORIENTATION_LANDSCAPE && mVideoURL.isEmpty()) {
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
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mPlaybackState = mExoPlayer.getPlayWhenReady();
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