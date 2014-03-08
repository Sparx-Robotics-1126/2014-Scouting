package org.gosparx.scouting.aerialassist.fragments.match;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.gosparx.scouting.aerialassist.R;

/**
 * Created by jbass on 2/25/14.
 */
public class FieldLocationSelectionFragment extends DialogFragment implements View.OnTouchListener {

    public static final String X_BUNDLE_KEY = "X";
    public static final String Y_BUNDLE_KEY = "Y";

    private ImageView imageView;

    private Point selectedLocation;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_field_location_selection, null, false);

        imageView = (ImageView) view.findViewById(R.id.imageViewLocationSelectionImage);
        imageView.setOnTouchListener(this);

        if(getArguments() != null
                && getArguments().containsKey(X_BUNDLE_KEY)
                && getArguments().containsKey(Y_BUNDLE_KEY))
            handleTouch(BitmapFactory.decodeResource(getResources(), R.drawable.field).copy(Bitmap.Config.RGB_565, true),
                    getArguments().getInt(X_BUNDLE_KEY),
                    getArguments().getInt(Y_BUNDLE_KEY));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.select_robot_location)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (selectedLocation != null) {
                            Intent data = new Intent();
                            data.putExtra(X_BUNDLE_KEY, selectedLocation.x);
                            data.putExtra(Y_BUNDLE_KEY, selectedLocation.y);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, data);
                        } else {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), -1, null);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), -1, null);
                        dismiss();
                    }
                })
                .create();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.field).copy(Bitmap.Config.RGB_565, true);
            handleTouch(tempBitmap,
                    motionEvent.getX()*tempBitmap.getWidth()/imageView.getWidth(),
                    motionEvent.getY()*tempBitmap.getHeight()/imageView.getHeight());
        }
        return true;
    }

    private void handleTouch(Bitmap tempBitmap, float x, float y) {
        selectedLocation = new Point((int) x, (int) y);
        Canvas c = new Canvas(tempBitmap);
        Paint paint = new Paint();
        paint.setColor(0xFF000000);
        c.drawCircle(selectedLocation.x, selectedLocation.y, 50, paint);
        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        imageView.invalidate();
    }
}
