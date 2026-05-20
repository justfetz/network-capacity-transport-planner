# Source Gap Notes

This public-safe planner is based on a real private network-capacity planning project, but one important source artifact was not present in the checked-in repository:

- `2023BaseCaseData.txt`

The original `Main.java` references that file as the primary demand input. Because it is not in the local repo snapshot, this public version cannot claim to preserve the original raw demand rows verbatim.

What *is* preserved here:

- the multi-site network shape
- staggered opening dates
- future capacity-adjustment logic
- two product-family lanes
- nearest-feasible assignment behavior
- serviced vs unserviced demand outputs

What is intentionally recreated with public-safe inputs:

- facility names
- scenario CSVs
- customer / demand examples
- coordinates tied to anonymized hubs rather than original enterprise labels

If the original demand file is recovered later, the correct next step is:

1. load it into a private staging copy
2. sanitize customer names and any company-specific identifiers
3. generalize or replace sensitive coordinates where needed
4. export a public-safe scenario pack into `input/`
