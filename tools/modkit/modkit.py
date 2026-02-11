#!/usr/bin/env python3
import argparse
import json
import shutil
import subprocess
import sys
from pathlib import Path
import zipfile

ROOT = Path(__file__).resolve().parents[2]
TEMPLATE = ROOT / "docs" / "mod_template"
ZIP_TEMPLATE = ROOT / "docs" / "mod_template_zip"

EXCLUDE_DIRS = {"build", ".git", "__pycache__"}
EXCLUDE_FILES = {".DS_Store"}


def die(msg, code=1):
    print(msg, file=sys.stderr)
    sys.exit(code)


def read_mod_json(mod_dir: Path):
    path = mod_dir / "mod.json"
    if not path.exists():
        die(f"Missing mod.json in {mod_dir}")
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except Exception as exc:
        die(f"Failed to read mod.json: {exc}")


def copy_template(dest: Path, use_zip_template: bool):
    src = ZIP_TEMPLATE if use_zip_template else TEMPLATE
    if not src.exists():
        die(f"Template not found: {src}")
    if dest.exists():
        die(f"Destination already exists: {dest}")
    shutil.copytree(src, dest)


def collect_java_files(src_dir: Path):
    return [p for p in src_dir.rglob("*.java") if p.is_file()]


def run(cmd, cwd=None):
    proc = subprocess.run(cmd, cwd=cwd)
    if proc.returncode != 0:
        die(f"Command failed: {' '.join(cmd)}", proc.returncode)


def build_jar(mod_dir: Path, classpath: str, javac: str, jar: str):
    mod_json = read_mod_json(mod_dir)
    jar_name = mod_json.get("jar", "mod.jar")
    src_dir = mod_dir / "code"
    if not src_dir.exists():
        die("No code/ directory found. Nothing to compile.")

    sources = collect_java_files(src_dir)
    if not sources:
        die("No .java sources found in code/")

    build_dir = mod_dir / "build" / "classes"
    if build_dir.exists():
        shutil.rmtree(build_dir)
    build_dir.mkdir(parents=True, exist_ok=True)

    cmd = [javac, "-encoding", "UTF-8", "-d", str(build_dir)]
    if classpath:
        cmd += ["-cp", classpath]
    cmd += [str(p) for p in sources]
    run(cmd)

    jar_path = mod_dir / jar_name
    if jar_path.exists():
        jar_path.unlink()
    run([jar, "cf", str(jar_path), "-C", str(build_dir), "."])
    print(f"Built jar: {jar_path}")


def should_include(path: Path):
    for part in path.parts:
        if part in EXCLUDE_DIRS:
            return False
    if path.name in EXCLUDE_FILES:
        return False
    if path.suffix.lower() == ".zip":
        return False
    return True


def pack_zip(mod_dir: Path, output: Path):
    if output.exists():
        output.unlink()

    with zipfile.ZipFile(output, "w", zipfile.ZIP_DEFLATED) as zf:
        for path in mod_dir.rglob("*"):
            if not path.is_file():
                continue
            if not should_include(path):
                continue
            rel = path.relative_to(mod_dir)
            zf.write(path, rel)

    print(f"Packed zip: {output}")


def write_enabled(mod_dir: Path, enabled: bool):
    path = mod_dir / "mod.enabled"
    path.write_text("true" if enabled else "false", encoding="utf-8")


def update_compat(mod_dir: Path, api_version=None, min_game=None, max_game=None):
    mod_json_path = mod_dir / "mod.json"
    data = read_mod_json(mod_dir)
    updated = False

    if api_version is not None:
        data["api_version"] = int(api_version)
        updated = True
    if min_game is not None:
        data["min_game_version"] = int(min_game)
        updated = True
    if max_game is not None:
        data["max_game_version"] = int(max_game)
        updated = True

    if updated:
        mod_json_path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def validate_mod(mod_dir: Path):
    errors = []
    mod = read_mod_json(mod_dir)
    allowed_item_types = {"ITEM", "MELEE_WEAPON", "MISSILE_WEAPON", "ARMOR"}

    if not mod.get("id"):
        errors.append("mod.json: missing id")
    if "api_version" in mod and not isinstance(mod["api_version"], int):
        errors.append("mod.json: api_version must be int")

    for key in ("min_game_version", "max_game_version"):
        if key in mod and not isinstance(mod[key], int):
            errors.append(f"mod.json: {key} must be int")

    items_path = None
    if "items" in mod and isinstance(mod["items"], str):
        items_path = mod_dir / mod["items"]
    elif "items_file" in mod and isinstance(mod["items_file"], str):
        items_path = mod_dir / mod["items_file"]

    if items_path:
        if not items_path.exists():
            errors.append(f"{items_path.name}: file not found")
        else:
            try:
                items_data = json.loads(items_path.read_text(encoding="utf-8"))
            except Exception as exc:
                errors.append(f"{items_path.name}: invalid JSON ({exc})")
                items_data = None

            if items_data is not None:
                items_array = items_data.get("items") if isinstance(items_data, dict) else items_data
                if not isinstance(items_array, list):
                    errors.append(f"{items_path.name}: expected items[] array")
                else:
                    for idx, item in enumerate(items_array):
                        if not isinstance(item, dict):
                            errors.append(f"{items_path.name}: item #{idx} is not an object")
                            continue
                        if not item.get("id"):
                            errors.append(f"{items_path.name}: item #{idx} missing id")
                        if not item.get("category"):
                            errors.append(f"{items_path.name}: item #{idx} missing category")
                        if "item_type" in item:
                            t = str(item.get("item_type", "")).strip().upper()
                            if t and t not in allowed_item_types:
                                errors.append(f"{items_path.name}: item #{idx} invalid item_type '{t}'")
    return errors


def main():
    parser = argparse.ArgumentParser(description="Mod kit for Experienced Pixel Dungeon")
    sub = parser.add_subparsers(dest="cmd", required=True)

    p_init = sub.add_parser("init", help="Create a new mod from template")
    p_init.add_argument("dest", help="Destination folder")
    p_init.add_argument("--data-only", action="store_true", help="Use data-only template")

    p_build = sub.add_parser("build-jar", help="Compile code/ into a mod jar")
    p_build.add_argument("mod_dir", help="Mod directory")
    p_build.add_argument("--classpath", default="", help="Classpath for game API")
    p_build.add_argument("--javac", default="javac", help="Path to javac")
    p_build.add_argument("--jar", default="jar", help="Path to jar tool")

    p_pack = sub.add_parser("pack", help="Package a mod folder into zip")
    p_pack.add_argument("mod_dir", help="Mod directory")
    p_pack.add_argument("--output", default="", help="Output zip path")
    p_pack.add_argument("--enable", action="store_true", help="Create mod.enabled=true before packing")
    p_pack.add_argument("--disable", action="store_true", help="Create mod.enabled=false before packing")
    p_pack.add_argument("--no-verify", action="store_true", help="Skip schema verification")
    p_pack.add_argument("--api-version", type=int, default=None, help="Set api_version in mod.json")
    p_pack.add_argument("--min-game", type=int, default=None, help="Set min_game_version in mod.json")
    p_pack.add_argument("--max-game", type=int, default=None, help="Set max_game_version in mod.json")

    p_all = sub.add_parser("build", help="Build jar (optional) and pack zip")
    p_all.add_argument("mod_dir", help="Mod directory")
    p_all.add_argument("--classpath", default="", help="Classpath for game API")
    p_all.add_argument("--javac", default="javac", help="Path to javac")
    p_all.add_argument("--jar", default="jar", help="Path to jar tool")
    p_all.add_argument("--output", default="", help="Output zip path")
    p_all.add_argument("--enable", action="store_true", help="Create mod.enabled=true before packing")
    p_all.add_argument("--disable", action="store_true", help="Create mod.enabled=false before packing")
    p_all.add_argument("--no-verify", action="store_true", help="Skip schema verification")
    p_all.add_argument("--api-version", type=int, default=None, help="Set api_version in mod.json")
    p_all.add_argument("--min-game", type=int, default=None, help="Set min_game_version in mod.json")
    p_all.add_argument("--max-game", type=int, default=None, help="Set max_game_version in mod.json")

    args = parser.parse_args()

    if args.cmd == "init":
        dest = Path(args.dest).expanduser().resolve()
        copy_template(dest, args.data_only)
        print(f"Created mod template at: {dest}")
        return

    mod_dir = Path(args.mod_dir).expanduser().resolve()
    if not mod_dir.exists():
        die(f"Mod directory not found: {mod_dir}")

    if args.cmd == "build-jar":
        build_jar(mod_dir, args.classpath, args.javac, args.jar)
        return

    output = Path(args.output).expanduser().resolve() if args.output else mod_dir.with_suffix(".zip")

    if args.cmd == "pack":
        if args.enable and args.disable:
            die("Choose only one of --enable or --disable")
        if args.api_version is not None or args.min_game is not None or args.max_game is not None:
            update_compat(mod_dir, args.api_version, args.min_game, args.max_game)
        if args.enable:
            write_enabled(mod_dir, True)
        if args.disable:
            write_enabled(mod_dir, False)
        if not args.no_verify:
            errors = validate_mod(mod_dir)
            if errors:
                for err in errors:
                    print(f"ERROR: {err}", file=sys.stderr)
                die("Validation failed.")
        pack_zip(mod_dir, output)
        return

    if args.cmd == "build":
        code_dir = mod_dir / "code"
        if code_dir.exists():
            build_jar(mod_dir, args.classpath, args.javac, args.jar)
        if args.enable and args.disable:
            die("Choose only one of --enable or --disable")
        if args.api_version is not None or args.min_game is not None or args.max_game is not None:
            update_compat(mod_dir, args.api_version, args.min_game, args.max_game)
        if args.enable:
            write_enabled(mod_dir, True)
        if args.disable:
            write_enabled(mod_dir, False)
        if not args.no_verify:
            errors = validate_mod(mod_dir)
            if errors:
                for err in errors:
                    print(f"ERROR: {err}", file=sys.stderr)
                die("Validation failed.")
        pack_zip(mod_dir, output)
        return


if __name__ == "__main__":
    main()
