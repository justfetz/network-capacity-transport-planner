# Network Capacity Transport Planner

[![Java CI](https://github.com/justfetz/network-capacity-transport-planner/actions/workflows/java-ci.yml/badge.svg)](https://github.com/justfetz/network-capacity-transport-planner/actions/workflows/java-ci.yml)

This project is a public-safe repurposing of a real-world plant network capacity planning use case.

Instead of exposing company names, exact operating assumptions, or embedded business identifiers, this version reframes the model as a general operations research problem:

- assign regional demand to a constrained facility network
- respect site opening and closing windows
- account for future capacity adjustments over time
- handle multiple product families
- report serviced and unserviced demand cleanly

## What the model does

The Java baseline assigns each demand point to the nearest feasible facility for the requested product family and month/year.

Each facility has:

- a neutral public name
- representative coordinates
- separate monthly capacities for `family_a` and `family_b`
- open and close dates
- optional future capacity changes

Each demand record includes:

- product family
- customer or region name
- city / state
- month / year
- coordinates
- demand volume

The output separates:

- serviced demand assignments
- unserviced demand
- remaining capacity by facility

## Public-safe design choices

- facility names are anonymized
- coordinates are representative, not copied from the original enterprise map
- all paths are relative
- sample data is synthetic
- the code is organized as a reusable solver rather than a one-off machine-local script

## Project structure

- `input/` sample scenarios and a larger legacy-shape scenario pack
- `output/` generated results
- `src/main/java/planner/` Java baseline
- `docs/` notes on sanitization, model intent, and source gaps

## Running

From the project root:

```powershell
javac -d out src/main/java/planner/*.java
java -cp out planner.Main
```

That will read the sample input files and write:

- `output/serviced_assignments.csv`
- `output/unserviced_demand.csv`
- `output/facility_capacity_snapshot.csv`

Or use the included helper script:

```powershell
.\java\run-java.ps1
```

You can also point the runner at a richer public-safe scenario pack:

```powershell
java -cp out planner.Main input/legacy_shape_facilities.csv input/legacy_shape_demand.csv
```

That legacy-shape scenario preserves the larger network footprint and timing structure from the original private model while using anonymized hub names and public-safe sample demand.

## Tests

A lightweight Java regression harness is included for the key public-safe rules:

- nearest feasible assignment
- unserviced demand when capacity is insufficient
- future capacity adjustment behavior
- facility opening window behavior
- legacy-shaped scenario load behavior

Run it with:

```powershell
.\java\run-java-tests.ps1
```

## Quality signals

- GitHub Actions runs the Java regression harness on every push and pull request.
- The current test layer is intentionally framework-light to keep the baseline easy to run locally.
- Code coverage is a logical next step once the repo moves to a fuller Java test stack such as JUnit + JaCoCo alongside the planned Python and OR-Tools parity work.

## Contribution flow

- Open a short-lived feature branch for each change.
- Keep the public-safe contract intact: no company names, private identifiers, or copied enterprise coordinates.
- Run `.\java\run-java-tests.ps1` before opening a pull request.
- Use pull requests even for solo work so the history stays reviewable.

See [CONTRIBUTING.md](CONTRIBUTING.md) for the lightweight project workflow.

## Next steps

- expand the Java regression harness with more scenario coverage
- add Python baseline parity
- add OR-Tools Python and OR-Tools Java variants
- extend the public formulation toward facility location / capacity expansion scenarios
- import a sanitized version of the original demand file if it is recovered from the private project archive
