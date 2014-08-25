package com.hengxuan.lens.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.LensConstant;
import com.hengxuan.lens.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CheckFileExpandListAdapter extends BaseExpandableListAdapter {
	public static final int ItemHeight = 48;
	public static final int PaddingLeft = 36;

	private MyGridView toolbarGrid;

	String path = Environment.getExternalStorageDirectory().toString()
			+ File.separator + "dxlphoto" + File.separator;

	File[] files = new File(path).listFiles();

	private ArrayList<ArrayList<String>> menu_toolbar_name_array = new ArrayList<ArrayList<String>>();

	private List<String> groupStrs;
	private Map<String, ArrayList<String>> groups;

	private HashMap<String, Bitmap> bitmapMap = new HashMap<String, Bitmap>();

	private Context parentContext;

	private LayoutInflater layoutInflater;

	private ProgressDialog progress;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			progress.dismiss();
		}

	};

	public CheckFileExpandListAdapter(Context view,
			final List<String> groupStrs,
			final Map<String, ArrayList<String>> groups) {
		parentContext = view;

		this.groupStrs = groupStrs;
		this.groups = groups;
		progress = ProgressDialog.show(parentContext, parentContext
				.getResources().getString(R.string.file), parentContext
				.getResources().getString(R.string.loading));
		new Thread() {
			public void run() {
				for (int i = 0; i < groupStrs.size(); i++) {
					// ArrayList<String> childList = new ArrayList<String>();
					ArrayList<String> childList = groups.get(groupStrs.get(i));
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inSampleSize = 32;
					for (int j = 0; j < childList.size(); j++) {
						Bitmap bitmap = Bitmap.createScaledBitmap(
								BitmapFactory.decodeFile(
										path + childList.get(j), opts), 100,
								100, false);
						bitmap = getRoundedCornerBitmap(bitmap, 20);
						bitmapMap.put(path + childList.get(j), bitmap);
					}

					menu_toolbar_name_array.add(childList);
				}
				handler.sendEmptyMessage(0);
			}
		}.start();

	}

	public Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		bitmap.recycle();
		return output;
	}

	public Object getChild(int groupPosition, int childPosition) {
		// return groups.get(groupStrs.get(groupPosition)).get(childPosition);
		return null;
	}

	public int getChildrenCount(int groupPosition) {
		// return groups.get(groupStrs.get(groupPosition)).size();
		return 1;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		layoutInflater = (LayoutInflater) parentContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.expand_child_grid, null);
		// convertView.setBackgroundColor(Color.rgb(10, 60, 100));
		toolbarGrid = (MyGridView) convertView.findViewById(R.id.expand_grid);
		toolbarGrid.setNumColumns(3);
		toolbarGrid.setGravity(Gravity.CENTER);
		toolbarGrid.setHorizontalSpacing(10);
		toolbarGrid.setAdapter(getMenuAdapter(menu_toolbar_name_array
				.get(groupPosition)));
		toolbarGrid.setOnItemClickListener(new onExpandItemClickListener(
				groupPosition));
		toolbarGrid
				.setOnItemLongClickListener(new onExpandItemLongClickListener(groupPosition));
		return convertView;
	}

	/**
	 * 可自定义list
	 */
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		layoutInflater = (LayoutInflater) parentContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.expand_group, null);
		TextView textView = (TextView) convertView.findViewById(R.id.textGroup);
		textView.setText(getGroup(groupPosition).toString());
		textView.setPadding(0, 20, 0, 20);
		textView.setTextSize(25);
		textView.setTextColor(Color.WHITE);
		textView.setBackgroundColor(Color.rgb(0, 110, 180));
		return convertView;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public Object getGroup(int groupPosition) {
		return groupStrs.get(groupPosition);
	}

	public int getGroupCount() {
		return groupStrs.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	private ImageAdapter getMenuAdapter(ArrayList<String> menuNameArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < menuNameArray.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", path + menuNameArray.get(i));
			String text = menuNameArray.get(i).split("_")[1];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date dt = new Date(Long.parseLong(text));
			String timeStr = sdf.format(dt);
			map.put("itemText", timeStr);
			data.add(map);
		}
		ImageAdapter imageAdapter = new ImageAdapter(parentContext, data,
				R.layout.grid_item, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return imageAdapter;
	}

	public class ImageAdapter extends SimpleAdapter {
		public ImageAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void setViewImage(ImageView v, String value) {
			// TODO Auto-generated method stub

			v.setImageBitmap(bitmapMap.get(value));

		}

		@Override
		public void setViewText(TextView v, String text) {
			// TODO Auto-generated method stub
			v.setGravity(Gravity.CENTER);
			// String newText = text.substring(0, 10) + "\n"
			// + text.substring(11, 16);
			// text = text.replace(" ", "\n");
			text = text.replaceFirst(" ", "\n");
			text = text.replaceFirst(" ", ":");
			String newText = text;// text.substring(0, 16);
			v.setText(newText);
			// v.setTextColor(Color.WHITE);
		}

	}

	/**
	 * 点击child item处理函数
	 * 
	 * @author Administrator
	 * 
	 */
	public class onExpandItemClickListener implements OnItemClickListener {

		private String name;
		private int groupPosition;

		public onExpandItemClickListener(int i) {
			// this.name = name;
			groupPosition = i;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			String file = menu_toolbar_name_array.get(groupPosition).get(arg2);
			String type = file.split("_")[0];
			Intent intent;
			switch (Integer.parseInt(type)) {
			case LensConstant.LEFT_EYE_INDEX:
				intent = new Intent(LensConstant.ANA_IRIS);
				intent.putExtra(LensConstant.PHOTO_PATH, path + file);
				intent.putExtra(LensConstant.EYES_INDEX, LensConstant.LEFT_EYE);
				parentContext.startActivity(intent);
				break;
			case LensConstant.RIGHT_EYE_INDEX:
				intent = new Intent(LensConstant.ANA_IRIS);
				intent.putExtra(LensConstant.PHOTO_PATH, path + file);
				intent.putExtra(LensConstant.EYES_INDEX, LensConstant.RIGHT_EYE);
				parentContext.startActivity(intent);
				break;
			case LensConstant.INDEX_HAIR:
				intent = new Intent(LensConstant.ANA_HAIR);
				intent.putExtra(LensConstant.PHOTO_PATH, path + file);
				parentContext.startActivity(intent);
				break;
			case LensConstant.INDEX_NAEVUS:
				intent = new Intent(LensConstant.ANA_NAEVUS);
				intent.putExtra(LensConstant.PHOTO_PATH, path + file);
				parentContext.startActivity(intent);
				break;
			case LensConstant.INDEX_SKIN:
				intent = new Intent(LensConstant.ANA_SKIN);
				intent.putExtra(LensConstant.PHOTO_PATH, path + file);
				parentContext.startActivity(intent);
				break;
			default:
				break;
			}

		}

	}

	public class onExpandItemLongClickListener implements
			OnItemLongClickListener {

		// private String name;
		private int groupPosition;

		public onExpandItemLongClickListener(int i) {
			// this.name = name;
			groupPosition = i;
		}

		@Override
		public boolean onItemLongClick(final AdapterView<?> parent, View view,
				final int position, long id) {
			// TODO Auto-generated method stub
			final AlertDialog alertdialog = (new AlertDialog.Builder(
					parentContext)).create();

			final int pos = position;
			alertdialog.setMessage(parentContext.getResources().getString(
					R.string.whether_delete_file));
			alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, parentContext
					.getResources().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

							// TODO Auto-generated method stub
							String filename = path
									+ menu_toolbar_name_array
											.get(groupPosition).get(position);
							File file = new File(filename);
							if (file.exists()) {
								if (file.isFile()) {
									file.delete();
								}
							}
							File filea = new File(filename.substring(0,
									filename.length() - 5) + "a.png");
							if (filea.exists()) {
								if (filea.isFile()) {
									filea.delete();
								}
							}
							menu_toolbar_name_array.get(groupPosition).remove(
									pos);
							bitmapMap.remove(filename);
							((MyGridView) parent)
									.setAdapter(getMenuAdapter(menu_toolbar_name_array
											.get(groupPosition)));
							if (menu_toolbar_name_array.get(groupPosition)
									.size() == 0) {
								menu_toolbar_name_array.remove(groupPosition);
								groups.remove(groupPosition);
								groupStrs.remove(groupPosition);
								// TODO
							}
							notifyDataSetChanged();
							alertdialog.dismiss();

						}
					});
			alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, parentContext
					.getResources().getString(R.string.no),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertdialog.cancel();
						}
					});
			alertdialog.show();

			return false;
		}

	}
}