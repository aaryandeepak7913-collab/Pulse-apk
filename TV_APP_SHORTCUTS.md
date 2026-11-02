# TV app shortcuts

Pulse adds a TV icon to the Bluetooth remote toolbar. Enter the TV's local IP address, select Google TV / Android TV or LG webOS, then tap one of the four app shortcuts.

## Google TV / Android TV

The current implementation uses the TV's DIAL launch service on TCP port 8008. It sends launch requests for Netflix, YouTube, Prime Video and JioHotstar aliases. DIAL is a launch-only protocol; a TV or app that does not expose a DIAL endpoint cannot be launched by this path. No pairing PIN is required by DIAL.

## LG webOS

Pulse connects to the TV's local SSAP WebSocket service on TCP port 3000. The first connection should show an authorization prompt on the TV. Accept it; Pulse stores the returned client key in private app preferences. Pulse asks the TV for its installed launch points and matches the requested app by its displayed title / app ID before launching it.

## Network requirements

The phone and TV must be on the same local network. Enter the TV's IP address without a port number. App launch requires the app to be installed on the TV. Compatibility depends on TV firmware, network settings and the app's exposed launch interface.
