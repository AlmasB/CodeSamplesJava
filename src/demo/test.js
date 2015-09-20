function myFunc() {
    var result = ["hi", "ho", "yoo"];
    
    var System = Java.type("java.lang.System");
    System.out.println("Inside JS");
    
    return System.nanoTime();
}

function helloFromJS() {
    return "Hello Java, I'm calling you from JavaScript";
}