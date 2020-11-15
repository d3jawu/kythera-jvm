const { buildAndEval } = require("./util.js");

let res;

describe("Structs", () => {
  beforeAll((done) => {
    buildAndEval("struct")
      .then((value) => {
        res = value;
        done();
      })
      .catch((reason) => {
        done(reason);
      });
  });

  test("Literal value", () => {
    // structs' value should be a reference to their field values
    expect(res.myStruct.value).toBe(res.myStruct.fieldValues);
    expect(res.myStruct.value.x.value).toBe(1);
    expect(res.myStruct.value.y.value).toBeCloseTo(1.01);
  });

  test("Struct type", () => {
    expect(res.structType.value.baseType).toEqual("STRUCT");
    expect(res.structType.value.typeMeta.fieldTypes.x.value.baseType).toEqual(
      "NUM"
    );
    expect(res.structType.value.typeMeta.fieldTypes.y.value.baseType).toEqual(
      "NUM"
    );
  });

  test("Dot access", () => {
    expect(res.structMember1.value).toEqual(1);
    expect(res.structMember2.value).toBeCloseTo(1.01);
  });

  test("Type interning", () => {
    expect(res.myStruct.typeValue).toEqual(res.myStruct2.typeValue);
    expect(res.structType.value.typeMeta.fieldTypes.x.value.baseType).toEqual(
      res.structType.value.typeMeta.fieldTypes.y.value.baseType
    );
  });
});
