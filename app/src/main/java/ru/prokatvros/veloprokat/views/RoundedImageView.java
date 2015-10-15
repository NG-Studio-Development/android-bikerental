package ru.prokatvros.veloprokat.views;

/*public class RoundedImageView extends ImageView {
	private Paint mBackground;
	private Paint mBorder;
	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mBackground = new Paint();
		mBackground.setColor(0xff363636);
		mBorder = new Paint();
		mBorder.setColor(0xfff18800);
		mBorder.setStrokeWidth(4);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        float cX = rect.width()/2;
        float cY = rect.height()/2;
        
        float r = cX - 8;
        clipPath.addCircle(cX, cY, r, Path.Direction.CW);
        canvas.drawCircle(cX, cY,r+2, mBorder);
        
        canvas.drawCircle(cX, cY, r, mBackground);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
	}

} */
