package travelan.art.sangeun.travelan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import travelan.art.sangeun.travelan.adapters.NewspeedListAdapter;
import travelan.art.sangeun.travelan.models.Newspeed;
import travelan.art.sangeun.travelan.models.User;
import travelan.art.sangeun.travelan.utils.ApiClient;
import travelan.art.sangeun.travelan.utils.BaseFragment;

/**
 * Created by sangeun on 2018-05-12.
 */

public class NewspeedFragment extends BaseFragment {
    private static final String TAG = "NewspeedFragment";
    private RecyclerView newspeedList;
    private NewspeedListAdapter adapter;
    private List<Newspeed> items = new ArrayList<>();
    private FloatingActionButton addBtn;
    private int currentPage = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_newspeed, container, false);

        newspeedList = rootView.findViewById(R.id.newspeedList);
        newspeedList.setLayoutManager(new LinearLayoutManager(getContext()));
        newspeedList.setItemAnimator(new DefaultItemAnimator());

        adapter = new NewspeedListAdapter(getContext(), items);
        newspeedList.setAdapter(adapter);

        addBtn = rootView.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),WriteNewspeedActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        getList(currentPage);
        return rootView;
    }

    @Override
    public void onFocus() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_newspeed, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mypage:
                Intent intent = new Intent(getContext(), MyPageActivity.class);
                startActivity(intent);
                break;
            case R.id.report:
                alertSMS();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void alertSMS() {
        ApiClient.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String name = response.getString("name");
                    String emergencyPhone = response.getString("emergency");

                    String msg = name + "(이) 가 위험합니다.";

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emergencyPhone, null, msg, null, null);
                        Toast.makeText(getContext(), "Message Sent",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Toast.makeText(getContext(),ex.getMessage().toString(),
                                Toast.LENGTH_LONG).show();
                        ex.printStackTrace();
                    }
                }catch(JSONException e){
                    Log.e("FAIL TO PARSE DATA", e.getMessage());
                }
            }
        });
    }

    private void getList(int page) {
        if (adapter.items.size() > 0 && page == currentPage) {
            return;
        }

        ApiClient.getNewspeeds(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray resultArray) {
                try {
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject object = resultArray.getJSONObject(i);

                        Newspeed item = new Newspeed();
                        item.id = object.getInt("id");
                        item.location = "#" + object.getJSONObject("travel").getString("title");
                        item.isFav = object.getBoolean("isFav");
                        item.contents = object.getString("content");

                        item.images = new ArrayList<>();
                        JSONArray images = object.getJSONArray("images");
                        for (int imageIndex = 0; imageIndex < images.length(); imageIndex++) {
                            JSONObject image = images.getJSONObject(imageIndex);
                            String imageUrl = image.getString("serverName") + image.getString("originName");
                            item.images.add(imageUrl);
                        }

                        if (!object.isNull("planId")) {
                            item.planId = object.getInt("planId");
                        }

                        item.user = new User();
                        JSONObject member = object.getJSONObject("member");
                        item.user.thumbnail = member.getString("thumb");
                        item.user.userId = member.getString("userId");

                        adapter.items.add(item);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("FAIL TO PARSE DATA", e.getMessage());
                    Toast.makeText(getContext(), "FAIL TO PARSE DATA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("On Failure", errorResponse.toString());
                Toast.makeText(getContext(), "FAIL TO LOAD DATA", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                adapter.items.clear();
                getList(0);
                newspeedList.scrollToPosition(0);
                break;
            default:
                break;
        }
    }
}
