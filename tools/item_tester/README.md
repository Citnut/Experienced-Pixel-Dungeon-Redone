# Item Tester

This tool runs a headless JUnit smoke test that instantiates every item listed in `Generator.Category` and validates basic metadata (name/desc/image/value) without launching the game UI.

## Run

```bash
./tools/item_tester/run_items_test.sh
```

## What it checks

- Every class registered in `Generator.Category` can be instantiated.
- `name()` and `desc()` return non-null strings.
- `image()` and `value()` can be queried without exceptions.

## Notes

- Uses libGDX headless backend for `Messages`/assets.
- Assets are included in the test classpath from `core/src/main/assets`.
