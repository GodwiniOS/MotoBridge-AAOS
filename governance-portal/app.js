const appExample = {
  spec_version: "1.0",
  app: {
    package: "com.example.driver",
    actions: ["OTP_INPUT", "TEXT_ENTRY", "MANUAL_ACTION"],
    capabilities: ["navigation", "background_work"]
  }
};

const oemExample = {
  spec_version: "1.0",
  oem: {
    name: "two-wheeler-strict",
    restrictions: {
      when_moving: {
        block: ["OTP_INPUT", "TEXT_ENTRY", "MANUAL_ACTION"]
      }
    },
    conditions: {
      ignition_required: true,
      always_moving: true
    }
  }
};

const appSpecInput = document.getElementById("appSpec");
const oemSpecInput = document.getElementById("oemSpec");
const statusBadge = document.getElementById("statusBadge");
const statusTitle = document.getElementById("statusTitle");
const statusBody = document.getElementById("statusBody");

function setBadge(label, statusClass) {
  statusBadge.textContent = label;
  statusBadge.classList.remove("compatible", "restricted", "incompatible");
  if (statusClass) {
    statusBadge.classList.add(statusClass);
  }
}

function parseSpec(text) {
  const trimmed = text.trim();
  if (!trimmed) {
    throw new Error("Spec input is empty.");
  }

  if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
    return JSON.parse(trimmed);
  }

  return parseYaml(trimmed);
}

function parseYaml(text) {
  const lines = text.split(/\r?\n/);
  const root = {};
  const stack = [{ indent: -1, value: root }];

  for (let i = 0; i < lines.length; i += 1) {
    const raw = lines[i];
    const line = raw.replace(/\t/g, "  ");
    if (!line.trim() || line.trim().startsWith("#")) {
      continue;
    }

    const indent = line.match(/^ */)[0].length;
    const trimmed = line.trim();

    while (stack.length > 1 && indent <= stack[stack.length - 1].indent) {
      stack.pop();
    }

    const parent = stack[stack.length - 1].value;

    if (trimmed.startsWith("- ")) {
      if (!Array.isArray(parent)) {
        throw new Error("YAML parser only supports arrays inside list blocks.");
      }
      const itemValue = parseScalar(trimmed.slice(2).trim());
      parent.push(itemValue);
      continue;
    }

    const splitIndex = trimmed.indexOf(":");
    if (splitIndex === -1) {
      throw new Error(`Invalid YAML line: ${trimmed}`);
    }

    const key = trimmed.slice(0, splitIndex).trim();
    let value = trimmed.slice(splitIndex + 1).trim();

    if (!value) {
      const nextLine = findNextNonEmpty(lines, i + 1);
      const isArray = nextLine && nextLine.trim().startsWith("-");
      value = isArray ? [] : {};
      parent[key] = value;
      stack.push({ indent, value });
    } else {
      parent[key] = parseScalar(value);
    }
  }

  return root;
}

function findNextNonEmpty(lines, start) {
  for (let i = start; i < lines.length; i += 1) {
    if (lines[i].trim() && !lines[i].trim().startsWith("#")) {
      return lines[i];
    }
  }
  return null;
}

function parseScalar(value) {
  if (value === "true") return true;
  if (value === "false") return false;
  if (!Number.isNaN(Number(value))) return Number(value);
  return value.replace(/^"|"$/g, "");
}

function normalizeApp(spec) {
  if (spec.app) return spec.app;
  return spec;
}

function normalizeOem(spec) {
  if (spec.oem) return spec.oem;
  return spec;
}

function evaluateCompatibility(appSpec, oemSpec) {
  const errors = [];
  const restrictions = [];
  const assumptions = [];

  const app = normalizeApp(appSpec);
  const oem = normalizeOem(oemSpec);

  if (!app?.package) errors.push("App spec missing package name.");
  if (!oem?.name) errors.push("OEM profile missing name.");

  const appActions = new Set(app?.actions || []);
  const blockedWhenMoving = new Set(oem?.restrictions?.when_moving?.block || []);

  const blockedActions = [...appActions].filter((action) => blockedWhenMoving.has(action));
  const alwaysMoving = oem?.conditions?.always_moving === true;

  if (blockedActions.length) {
    if (alwaysMoving) {
      errors.push(`Blocked actions for always-moving profile: ${blockedActions.join(", ")}.`);
    } else {
      restrictions.push(`Block while moving: ${blockedActions.join(", ")}.`);
    }
  }

  if (oem?.conditions?.ignition_required === true) {
    assumptions.push("App must respect ignition-required constraint.");
  }

  let status = "SAFE";
  if (errors.length) {
    status = "REJECTED";
  } else if (restrictions.length) {
    status = "RESTRICTED";
  }

  return { status, errors, restrictions, assumptions };
}

function renderResults(results) {
  if (!results) return;

  if (results.status === "SAFE") {
    setBadge("SAFE", "compatible");
  } else if (results.status === "RESTRICTED") {
    setBadge("RESTRICTED", "restricted");
  } else {
    setBadge("REJECTED", "incompatible");
  }

  statusTitle.textContent = results.status;
  statusBody.innerHTML = "";

  const sections = [];
  if (results.errors.length) {
    sections.push({ title: "Violations", items: results.errors });
  }
  if (results.restrictions.length) {
    sections.push({ title: "Restrictions", items: results.restrictions });
  }
  if (results.assumptions.length) {
    sections.push({ title: "Conditions", items: results.assumptions });
  }

  if (!sections.length) {
    statusBody.innerHTML = "<p>No restrictions detected for this pairing.</p>";
    return;
  }

  sections.forEach((section) => {
    const wrapper = document.createElement("div");
    const title = document.createElement("p");
    title.textContent = section.title;
    title.style.fontWeight = "700";
    title.style.marginBottom = "6px";
    const list = document.createElement("ul");
    section.items.forEach((item) => {
      const li = document.createElement("li");
      li.textContent = item;
      list.appendChild(li);
    });
    wrapper.appendChild(title);
    wrapper.appendChild(list);
    statusBody.appendChild(wrapper);
  });
}

function handleEvaluate() {
  try {
    const appSpec = parseSpec(appSpecInput.value);
    const oemSpec = parseSpec(oemSpecInput.value);
    const results = evaluateCompatibility(appSpec, oemSpec);
    renderResults(results);
  } catch (error) {
    setBadge("INPUT ERROR", "incompatible");
    statusTitle.textContent = "Invalid spec input";
    statusBody.innerHTML = `<p>${error.message}</p>`;
  }
}

function loadExample(target, example) {
  target.value = JSON.stringify(example, null, 2);
}

document.getElementById("evaluate").addEventListener("click", handleEvaluate);
document.getElementById("loadApp").addEventListener("click", () => {
  loadExample(appSpecInput, appExample);
});
document.getElementById("loadOem").addEventListener("click", () => {
  loadExample(oemSpecInput, oemExample);
});

loadExample(appSpecInput, appExample);
loadExample(oemSpecInput, oemExample);
renderResults({
  status: "RESTRICTED",
  errors: [],
  restrictions: ["Load specs and press Evaluate to see real output."],
  assumptions: []
});
