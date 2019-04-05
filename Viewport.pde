class Viewport {
  int view_w;
  int view_h;
  int view_size;
  int view_off_w = 0, view_off_h = 0;
  PGraphics bg;

  Viewport(PGraphics pg, int _view_size) {
    view_size = _view_size;
  }

  void display(PGraphics pg, int x, int y) {
    pushMatrix();
    translate(x, y);
    noStroke();
    fill(255);
    drawPointers();
    fill(100);

    if (viewport_show_alpha) image(bg, view_off_w, view_off_h, view_w, view_h);
    image(pg, view_off_w, view_off_h, view_w, view_h);
    popMatrix();
    //println(view_w, view_h);
  }

  void resize(PGraphics pg) {
    float aspect = 1.;
    view_w = view_size;
    view_h = view_size;
    view_off_w = 0;
    view_off_h = 0;

    aspect = float( min(pg.width, pg.height)) / float( max(pg.width, pg.height));
    if (pg.width > pg.height) {
      view_h *= aspect;
      view_off_h = (view_size-view_h)/2;
    } else if (pg.height > pg.width) { 
      view_w *= aspect;
      view_off_w = (view_size-view_w)/2;
    }
    bg = createAlphaBackground(view_w, view_h);
  }

  PGraphics createAlphaBackground(int w, int h) {

    PGraphics abg = createGraphics(w, h, P2D);
    int s = 10; // size of square
    abg.beginDraw();
    abg.background(127+50);
    abg.noStroke();
    abg.fill(127-50);
    for (int x = 0; x < w; x+=s+s) {
      for (int y = 0; y < h; y+=s+s) {
        abg.rect(x, y, s, s);
      }
    }
    for (int x = s; x < w; x+=s+s) {
      for (int y = s; y < h; y+=s+s) {
        abg.rect(x, y, s, s);
      }
    }
    abg.endDraw();
    return abg;
  }

  void drawPointers() {
    int x = view_off_w;
    int y = view_off_h;
    triangle(x, y, x-5, y, x, y-5);
    x += bg.width;
    triangle(x, y, x+5, y, x, y-5);
    y += bg.height;
    triangle(x, y, x+5, y, x, y+5);
    x = view_off_w;
    triangle(x, y, x-5, y, x, y+5);
  }
}

void updateCanvas() {
  c = createGraphics(cw, ch, P3D);
  view.resize(c);
}

void updateCanvas(int w, int h) {
  c = createGraphics(w, h, P3D);
  view.resize(c);
}
