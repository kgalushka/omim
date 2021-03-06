package com.mapswithme.maps.news;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapswithme.maps.BuildConfig;
import com.mapswithme.maps.Framework;
import com.mapswithme.maps.R;
import com.mapswithme.maps.base.BaseMwmDialogFragment;
import com.mapswithme.util.Counters;
import com.mapswithme.util.UiUtils;

public class WelcomeDialogFragment extends BaseMwmDialogFragment implements View.OnClickListener
{
  @Nullable
  private PolicyAgreementListener mListener;

  public static void show(@NonNull FragmentActivity activity)
  {
    create(activity);
    Counters.setFirstStartDialogSeen();
  }

  public static boolean isFirstLaunch(@NonNull FragmentActivity activity)
  {
    if (Counters.getFirstInstallVersion() < BuildConfig.VERSION_CODE)
      return false;

    FragmentManager fm = activity.getSupportFragmentManager();
    if (fm.isDestroyed())
      return false;

    return !Counters.isFirstStartDialogSeen();
  }

  private static void create(@NonNull FragmentActivity activity)
  {
    final WelcomeDialogFragment fragment = new WelcomeDialogFragment();
    activity.getSupportFragmentManager()
            .beginTransaction()
            .add(fragment, WelcomeDialogFragment.class.getName())
            .commitAllowingStateLoss();
  }

  public static boolean recreate(@NonNull FragmentActivity activity)
  {
    FragmentManager fm = activity.getSupportFragmentManager();
    Fragment f = fm.findFragmentByTag(WelcomeDialogFragment.class.getName());
    if (f == null)
      return false;

    // If we're here, it means that the user has rotated the screen.
    // We use different dialog themes for landscape and portrait modes on tablets,
    // so the fragment should be recreated to be displayed correctly.
    fm.beginTransaction()
      .remove(f)
      .commitAllowingStateLoss();
    fm.executePendingTransactions();
    return true;
  }

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    if (activity instanceof BaseNewsFragment.NewsDialogListener)
      mListener = (PolicyAgreementListener) activity;
  }

  @Override
  public void onDetach()
  {
    mListener = null;
    super.onDetach();
  }

  @Override
  protected int getCustomTheme()
  {
    return getFullscreenTheme();
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    Dialog res = super.onCreateDialog(savedInstanceState);
    res.requestWindowFeature(Window.FEATURE_NO_TITLE);
    res.setCancelable(false);

    View content = View.inflate(getActivity(), R.layout.fragment_welcome, null);
    res.setContentView(content);
    content.findViewById(R.id.btn__continue).setOnClickListener(this);
    ImageView image = content.findViewById(R.id.iv__image);
    image.setImageResource(R.drawable.img_welcome);
    TextView title = content.findViewById(R.id.tv__title);
    title.setText(R.string.onboarding_welcome_title);
    TextView subtitle = content.findViewById(R.id.tv__subtitle1);
    subtitle.setText(R.string.onboarding_welcome_first_subtitle);
    TextView terms = content.findViewById(R.id.tv__subtitle2);
    UiUtils.show(terms);
    Resources rs = content.getResources();
    terms.setText(Html.fromHtml(rs.getString(R.string.onboarding_welcome_second_subtitle,
        Framework.nativeGetTermsOfUseLink(), Framework.nativeGetPrivacyPolicyLink())));
    terms.setMovementMethod(LinkMovementMethod.getInstance());

    return res;
  }

  @Override
  public void onClick(View v)
  {
    if (v.getId() != R.id.btn__continue)
      return;

    if (mListener != null)
      mListener.onPolicyAgreementApplied();
    dismiss();
  }

  @Override
  public void onCancel(DialogInterface dialog)
  {
    super.onCancel(dialog);
    requireActivity().finish();
  }

  public interface PolicyAgreementListener
  {
    void onPolicyAgreementApplied();
  }
}
