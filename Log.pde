class Log {
  String current_log;
  int counter;
  Log() {
    current_log = "No new events";
    counter = 30;
  }

  void update() {
    fill(5);
    text(current_log, 10, height-10);
  }

  void setText(String input) {
    String time = zeroFormat(hour()) + ":" + zeroFormat(minute()) + ":" + zeroFormat(second());
    current_log = time + " " + input;
  }
}

String zeroFormat(int input) {
  String output = Integer.toString(input); 
  if (input < 10) output = "0" + output;
  return output;
}
