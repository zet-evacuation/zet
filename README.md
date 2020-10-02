# ZET Evacuation Tool

[![License: GPL v2](https://img.shields.io/badge/license-GPL%20(%3E%3D%202)-blue.svg)][GPL 2.0 license]
[![Build Status](https://travis-ci.org/zet-evacuation/zet.svg?branch=master)](https://travis-ci.org/zet-evacuation/zet)
[![codecov](https://codecov.io/gh/zet-evacuation/zet/branch/master/graph/badge.svg)](https://codecov.io/gh/zet-evacuation/zet)

The ZET softwaretools, which is licensed under the GPL. allows to model evacuation scenarios and simulate and optimze
them. Within ZET multi-storey buildings can be designed and residents can be placed and emergency exits can be
assigned to them.

An earliest arrival flow is then computed to route the residents to the best exits. Since the flow computation has to
make certain idealistic assumptions, the included simulation can then be used to test the computed evacuation paths.
A wide selection of statistics and an advanced visualizer help to analyze the data.

## Editor

ZET has an integrated editor that can be used to design the evacuation scenarios. With this editor it is possible to
create various floors, rooms on the floors and specialized areas within the rooms that have additional semantics. All
rooms and areas can be arbitrary closed polygons.

The evacuees can be automatically set in so called "assignment areas". The residents can be divided into groups that
have different properties. The properties and groups can be edited in the assignment editor, the adjustment of the
properties is done by several property distributions.

## Visualization

 Both, the simulaton on a cellular automaton and the calculated network flow can be visualized in 3d using OpenGL. The
position of the camera inside the visualization is completely controllable using the normal mouse and keyboard
controls. Within the visualization various information can be accessed, such as for example the utilization of areas.
The visualization can be viewed in perspective view, isometric view and orthogonal projection.

## License

This project is [licensed](LICENSE) under the terms of the [GPL 2.0 license] or later.

[GPL 2.0 license]: https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
