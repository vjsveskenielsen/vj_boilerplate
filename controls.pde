void controlSetup() {
  cp5 = new ControlP5(this);
  int xoff = 10;
  int yoff = 10;

  field_cw = cp5.addTextfield("field_cw")
    .setPosition(xoff, yoff)
    .setSize(30, 20)
    .setAutoClear(false)
    .setText(Integer.toString(cw))
    .setLabel("width")
    ;

  xoff += field_cw.getWidth() + 10;
  field_ch = cp5.addTextfield("field_ch")
    .setPosition(xoff, yoff)
    .setSize(30, 20)
    .setAutoClear(false)
    .setText(Integer.toString(ch))
    .setLabel("height")
    ;
  xoff += field_ch.getWidth() + 10;
  field_syphon_name = cp5.addTextfield("field_syphon_name")
    .setPosition(xoff, yoff)
    .setSize(60, 20)
    .setAutoClear(false)
    .setText(syphon_name)
    .setLabel("syphon name")
    ;

  xoff += field_syphon_name.getWidth() + 10;
  cp5.addToggle("viewport_show_alpha")
    .setPosition(xoff, yoff)
    .setSize(50, 20)
    .setValue(viewport_show_alpha)
    .setLabel("alpha / none")
    .setMode(ControlP5.SWITCH)
    ;

  xoff += cp5.getController("viewport_show_alpha").getWidth() + 10;  
  button_ip = cp5.addButton("button_ip")
    .setPosition(xoff, yoff)
    .setSize(70, 20)
    .setLabel("ip: " + ip)
    .setSwitch(false)
    ;

  xoff += button_ip.getWidth() + 10;
  field_osc_port = cp5.addTextfield("field_osc_port")
    .setPosition(xoff, yoff)
    .setSize(30, 20)
    .setAutoClear(false)
    .setText(Integer.toString(port))
    .setLabel("osc port")
    ;

  xoff += field_osc_port.getWidth() + 10;  
  field_osc_address = cp5.addTextfield("field_osc_address")
    .setPosition(xoff, yoff)
    .setSize(50, 20)
    .setAutoClear(false)
    .setText(syphon_name)
    .setLabel("osc address")
    ;

  xoff += field_osc_address.getWidth() + 10;
  cp5.addToggle("log_osc")
    .setPosition(xoff, yoff)
    .setSize(30, 20)
    .setLabel("log osc")
    .setValue(true)
    ;

  xoff = (int)button_ip.getPosition()[0];
  yoff += 40;
  cp5.addScrollableList("dropdown_midi")
    .setPosition(xoff, yoff)
    .setSize(100, 100)
    .setOpen(false)
    .setBarHeight(20)
    .setItemHeight(20)
    .addItems(Arrays.asList(midi_devices))
    .setLabel("MIDI INPUT")
    // .setType(ScrollableList.LIST) // currently supported DROPDOWN and LIST
    ;

  xoff += cp5.getController("dropdown_midi").getWidth() + 10;
  cp5.addToggle("log_midi")
    .setPosition(xoff, yoff)
    .setSize(30, 20)
    .setLabel("log midi")
    .setValue(true)
    ;

  // CUSTOM CONTROLS
  xoff = 10;
  yoff = 300;
  cp5.addSlider("n")
    .setPosition(xoff, yoff)
    .setSize(100, 15)
    .setRange(1, 30)
    .setValue(10)
    ;

  yoff += cp5.getController("n").getHeight() + 10;
  cp5.addKnob("speed1")
    .setPosition(xoff, yoff)
    .setSize(50, 50)
    .setRange(-.5, .5)
    .setValue(.02)
    ;
    
  yoff += cp5.getController("n").getHeight() + 10;
  cp5.addKnob("x1_range1")
    .setPosition(xoff, yoff)
    .setSize(50, 50)
    .setRange(0., 1.)
    .setValue(.25)
    ;
  yoff += 20;
  cp5.addKnob("x1_range2")
    .setPosition(xoff, yoff)
    .setSize(50, 50)
    .setRange(0., 1.)
    .setValue(.25)
    ;  
  yoff += 20;
  cp5.addSlider("y1_range1")
    .setPosition(xoff, yoff)
    .setSize(100, 15)
    .setRange(0., 1.)
    .setValue(.25)
    .setId(1)
    ;
  yoff += 20;
  cp5.addSlider("y1_range2")
    .setPosition(xoff, yoff)
    .setSize(100, 15)
    .setRange(0., 1.)
    .setValue(.25)
    ;

  yoff = 330;
  xoff += 150;

  cp5.addSlider("speed2")
    .setPosition(xoff, yoff)
    .setSize(100, 15)
    .setRange(-.5, .5)
    .setValue(.02)
    ;
  yoff += 20;
  cp5.addSlider("x2_range1")
    .setPosition(xoff, yoff)
    .setSize(100, 15)
    .setRange(0., 1.)
    .setValue(.25)
    ;
  yoff += 20;
  cp5.addSlider("x2_range2")
    .setPosition(xoff, yoff)
    .setSize(100, 15)
    .setRange(0., 1.)
    .setValue(.25)
    ;  
  yoff += 20;
  cp5.addSlider("y2_range1")
    .setPosition(xoff, yoff)
    .setSize(100, 15)
    .setRange(0., 1.)
    .setValue(.25)
    ;
  yoff += 20;
  cp5.addSlider("y2_range2")
    .setPosition(xoff, yoff)
    .setSize(100, 15)
    .setRange(0., 1.)
    .setValue(.25)
    ;
}

int evalFieldInput1(String in, int current, Controller con) {
  String name = con.getLabel();
  int out = -1;
  char[] ints = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
  char[] input = in.toCharArray();

  String txt = "value not int between 1 and 9999";
  if (input.length < 5) {
    int check = 0;
    for (char ch : input) {
      for (char i : ints) {
        if (ch == i) check++;
      }
    }

    if (input.length == check) {
      int verified_int = Integer.parseInt(in);
      txt = name + " changed from " + current + " to " + verified_int;
      if (verified_int < 1) { 
        verified_int = 1;
        txt = name + " was lower than 0 and defaults to " + verified_int;
      }
      if (verified_int == current) txt = "value is not different from " + current;
      else {
        out = verified_int;
      }
    }
  }
  log.setText(txt);

  return out;
}

boolean evalFieldInput2(String in, String current, Controller con) {
  String name = con.getLabel();
  String txt = "input to " + name + " is unchanged";
  boolean out = true;
  char[] illegal_chars = {'/', ',', '.', '(', ')', '[', ']', 
    '{', '}', ' '
  };
  char[] input = in.toCharArray();
  if (!in.equals(current)) {
    if (input.length > 0) {
      for (char ch : input) {
        for (char i : illegal_chars) {
          if (ch == i) {
            txt = "input to " + name + " contained illegal character and was reset";
            out = false;
          }
        }
      }
    }
  }
  log.setText(txt);

  return out;
}

public void field_cw(String theText) {
  int value = evalFieldInput1(theText, cw, cp5.getController("field_cw"));
  if (value > 0) {
    cw = value;
    updateCanvas();
  }
}
public void field_ch(String theText) {
  int value = evalFieldInput1(theText, ch, cp5.getController("field_ch"));
  if (value > 0) {
    ch = value;
    updateCanvas();
  }
}

public void field_syphon_name(String input) {
  if (evalFieldInput2(input, syphon_name, field_syphon_name)) {
    syphon_name = input;
    field_osc_address.setText(input);
    osc_address = input;
    log.setText("syphon name and osc address set to " + input);
  } else field_syphon_name.setText(syphon_name);
}

public void field_osc_address(String input) {
  if (evalFieldInput2(input, osc_address, field_osc_address)) {
    syphon_name = input;
    log.setText("osc address set to " + input);
  } else field_osc_address.setText(osc_address);
}

void dropdown_midi(int n) {
  updateMIDI(n);
  println("added " + midi_devices[n], n);
}

void log_midi(boolean state) {
  log_midi = state;
  if (state) log.setText("started logging midi input");
  else log.setText("stopped logging midi input");
}

void field_osc_port(String theText) {
  int value = evalFieldInput1(theText, port, field_osc_port);
  if (value > 0) {
    port = value;
    updateOSC(port);
  }
}

public void button_ip() {
  updateIP();
  log.setText("ip adress has been updated to " + ip);
}
