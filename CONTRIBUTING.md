# Contributing

This repo is meant to be run in the open, even when the contributor set is small. The goal is to make changes visible, reviewable, and safe to share.

## Workflow

1. Create a short-lived branch from `main`.
2. Make a focused change.
3. Run the local Java regression harness:

```powershell
.\java\run-java-tests.ps1
```

4. Open a pull request with:
   - what changed
   - why it changed
   - how you tested it
   - whether any source data or names were sanitized

## Public-safe rules

Do not introduce:

- company names
- private customer identifiers
- copied production coordinates that are tied to the original enterprise labels
- machine-local output paths
- raw private datasets

Do preserve:

- the model shape
- the scenario logic
- the timing logic
- the distinction between serviced and unserviced demand

## Testing expectations

At minimum, keep the regression harness passing.

Current checks cover:

- nearest feasible assignment
- insufficient-capacity behavior
- future capacity adjustments
- opening-window behavior
- loading the larger legacy-shaped scenario pack

As the repo grows, prefer adding deterministic tests alongside each new scenario rule.
