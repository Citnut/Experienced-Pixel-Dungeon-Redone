/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Blending;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Point;

//essentially contains a libGDX text input field, plus a PD-rendered background
public class TextInput extends Component {

	private Stage stage;
	private Container container;
	private TextField textField;
	private final GlyphLayout ghostLayout = new GlyphLayout();

	private Skin skin;

	private NinePatch bg;
	private String inlineSuggestion = "";
	private int inlineSuggestionColor = 0x8F8F8F;
	private float inlineSuggestionAlpha = 0.45f;

	public TextInput( NinePatch bg, boolean multiline, int size ){
		super();
		this.bg = bg;
		add(bg);

		//use a custom viewport here to ensure stage camera matches game camera
		Viewport viewport = new Viewport() {};
		viewport.setWorldSize(Game.width, Game.height);
		viewport.setScreenBounds(0, Game.bottomInset, Game.width, Game.height);
		viewport.setCamera(new OrthographicCamera());
		stage = new Stage(viewport);
		Game.inputHandler.addInputProcessor(stage);

		container = new Container<TextField>();
		stage.addActor(container);
		container.setTransform(true);

		skin = new Skin(FileUtils.getFileHandle(Files.FileType.Internal, "gdx/textfield.json"));

		TextField.TextFieldStyle style = skin.get(TextField.TextFieldStyle.class);
		style.font = Game.platform.getFont(size, "", false, false);
		style.background = null;
		if (multiline){
			textField = new TextArea("", style){
				@Override
				public void cut() {
					super.cut();
					onClipBoardUpdate();
				}

				@Override
				public void copy() {
					super.copy();
					onClipBoardUpdate();
				}
			};
		} else {
			textField = new TextField("", style){
				@Override
				public void cut() {
					super.cut();
					onClipBoardUpdate();
				}

				@Override
				public void copy() {
					super.copy();
					onClipBoardUpdate();
				}

				@Override
				public void draw(Batch batch, float parentAlpha) {
					super.draw(batch, parentAlpha);
					drawInlineSuggestion(batch, parentAlpha, this, getTextY(getStyle().font, getStyle().background));
				}
			};
		}
		textField.setProgrammaticChangeEvents(true);

		textField.addCaptureListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.TAB && onTabPressed()) {
					event.stop();
					return true;
				}
				if (keycode == Input.Keys.RIGHT && onRightPressed()) {
					event.stop();
					return true;
				}
				return false;
			}
		});

		textField.setTextFieldFilter(new TextField.TextFieldFilter() {
			@Override
			public boolean acceptChar(TextField field, char c) {
				if (c == '\t') {
					onTabPressed();
					return false;
				}
				return true;
			}
		});

		if (!multiline) textField.setAlignment(Align.center);

		textField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BitmapFont f = Game.platform.getFont(size, textField.getText(), false, false);
				TextField.TextFieldStyle style = textField.getStyle();
				if (f != style.font){
					style.font = f;
					textField.setStyle(style);
				}
				onChanged();
			}
		});

		if (!multiline){
			textField.setTextFieldListener(new TextField.TextFieldListener(){
				public void keyTyped (TextField textField, char c){
					if (c == '\r' || c == '\n'){
						enterPressed();
					}
				}

			});
		}

		textField.setOnscreenKeyboard(new TextField.OnscreenKeyboard() {
			@Override
			public void show(boolean visible) {
				Game.platform.setOnscreenKeyboardVisible(visible);
			}
		});

		container.setActor(textField);
		stage.setKeyboardFocus(textField);
		Game.platform.setOnscreenKeyboardVisible(true);
	}

	public void enterPressed(){
		//fires any time enter is pressed, do nothing by default
	};

	public void onChanged(){
		//fires any time the text box is changed, do nothing by default
	}

	public void onClipBoardUpdate(){
		//fires any time the clipboard is updated via cut or copy, do nothing by default
	}

	public void tabPressed(){
		//fires when tab is pressed, do nothing by default
	}

	public boolean onTabPressed(){
		tabPressed();
		return false;
	}

	public boolean onRightPressed(){
		//fires when right arrow is pressed, return true to consume key input
		return false;
	}

	public void setAlignment(int alignment){
		textField.setAlignment(alignment);
	}

	public void setTextColor(int rgbColor){
		TextField.TextFieldStyle style = textField.getStyle();
		style.fontColor = new Color(
				((rgbColor >> 16) & 0xFF) / 255f,
				((rgbColor >> 8) & 0xFF) / 255f,
				(rgbColor & 0xFF) / 255f,
				1f);
		textField.setStyle(style);
	}

	public void requestFocus(){
		if (stage != null) stage.setKeyboardFocus(textField);
	}

	public boolean hasFocus(){
		return stage != null && stage.getKeyboardFocus() == textField;
	}

	public void setText(String text){
		textField.setText(text);
		textField.setCursorPosition(textField.getText().length());
	}

	public void setMaxLength(int maxLength){
		textField.setMaxLength(maxLength);
	}

	public String getText(){
		return textField.getText();
	}

	public int getCursorPosition() {
		return textField.getCursorPosition();
	}

	public void setCursorPosition(int cursorPosition) {
		int length = textField.getText() == null ? 0 : textField.getText().length();
		textField.setCursorPosition(Math.max(0, Math.min(cursorPosition, length)));
	}

	public void setInlineSuggestion(String suggestion){
		inlineSuggestion = suggestion == null ? "" : suggestion;
	}

	public String getInlineSuggestion() {
		return inlineSuggestion;
	}

	public void setInlineSuggestionStyle(int rgbColor, float alpha){
		inlineSuggestionColor = rgbColor & 0xFFFFFF;
		inlineSuggestionAlpha = Math.max(0f, Math.min(1f, alpha));
	}

	public boolean acceptInlineSuggestion(){
		if (inlineSuggestion == null || inlineSuggestion.isEmpty()) return false;
		String text = textField.getText();
		int cursor = textField.getCursorPosition();
		textField.setText(text.substring(0, cursor) + inlineSuggestion + text.substring(cursor));
		textField.setCursorPosition(cursor + inlineSuggestion.length());
		return true;
	}

	private void drawInlineSuggestion(Batch batch, float parentAlpha, TextField field, float textY) {
		if (inlineSuggestion == null || inlineSuggestion.isEmpty()) return;
		if (field.getCursorPosition() != field.getText().length()) return;
		if (!field.hasKeyboardFocus()) return;

		TextField.TextFieldStyle style = field.getStyle();
		BitmapFont font = style.font;
		if (font == null) return;

		Drawable background = style.background;
		float leftPadding = background == null ? 0f : background.getLeftWidth();
		float rightPadding = background == null ? 0f : background.getRightWidth();
		float availableWidth = field.getWidth() - leftPadding - rightPadding;

		ghostLayout.setText(font, field.getText());
		float ghostStartX = field.getX() + leftPadding + ghostLayout.width;
		float maxX = field.getX() + leftPadding + availableWidth;

		if (ghostStartX >= maxX) return;

		String ghostText = inlineSuggestion;
		ghostLayout.setText(font, ghostText);
		while (!ghostText.isEmpty() && ghostStartX + ghostLayout.width > maxX) {
			ghostText = ghostText.substring(0, ghostText.length() - 1);
			ghostLayout.setText(font, ghostText);
		}
		if (ghostText.isEmpty()) return;

		float oldR = font.getColor().r;
		float oldG = font.getColor().g;
		float oldB = font.getColor().b;
		float oldA = font.getColor().a;

		float r = ((inlineSuggestionColor >> 16) & 0xFF) / 255f;
		float g = ((inlineSuggestionColor >> 8) & 0xFF) / 255f;
		float b = (inlineSuggestionColor & 0xFF) / 255f;
		// low alpha and neutral color gives a "ghost text" look without competing with the real input.
		font.setColor(r, g, b, inlineSuggestionAlpha * parentAlpha);
		font.draw(batch, ghostText, ghostStartX, field.getY() + textY);
		font.setColor(oldR, oldG, oldB, oldA);
	}

	public void copyToClipboard(){
		if (textField.getSelection().isEmpty()) {
			textField.selectAll();
		}

		textField.copy();
	}

	public void pasteFromClipboard(){
		String contents = Gdx.app.getClipboard().getContents();
		if (contents == null) return;

		if (!textField.getSelection().isEmpty()){
			//just use cut, but override clipboard
			textField.cut();
			Gdx.app.getClipboard().setContents(contents);
		}

		String existing = textField.getText();
		int cursorIdx = textField.getCursorPosition();

		textField.setText(existing.substring(0, cursorIdx) + contents + existing.substring(cursorIdx));
		textField.setCursorPosition(cursorIdx + contents.length());
	}

	@Override
	protected void layout() {
		super.layout();

		float contX = x;
		float contY = y;
		float contW = width;
		float contH = height;

		if (bg != null){
			bg.x = x;
			bg.y = y;
			bg.size(width, height);

			contX += bg.marginLeft();
			contY += bg.marginTop();
			contW -= bg.marginHor();
			contH -= bg.marginVer();
		}

		float zoom = Camera.main.zoom;
		Camera c = camera();
		if (c != null){
			zoom = c.zoom;
			Point p = c.cameraToScreen(contX, contY);
			contX = p.x/zoom;
			contY = p.y/zoom;
		}

		container.align(Align.topLeft);
		container.setPosition(contX*zoom, (Game.height-(contY*zoom)));
		container.size(contW*zoom, contH*zoom);
	}

	@Override
	public void update() {
		super.update();
		stage.act(Game.elapsed);
	}

	@Override
	public void draw() {
		super.draw();
		Quad.releaseIndices();
		Script.unuse();
		Texture.clear();
		stage.draw();
		Quad.bindIndices();
		Blending.useDefault();
	}

	@Override
	public synchronized void destroy() {
		super.destroy();
		if (stage != null) {
			stage.dispose();
			skin.dispose();
			Game.inputHandler.removeInputProcessor(stage);
			Game.platform.setOnscreenKeyboardVisible(false);
			if (!DeviceCompat.isDesktop()) Game.platform.updateSystemUI();
		}
	}
}
