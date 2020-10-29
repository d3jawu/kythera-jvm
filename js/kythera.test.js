describe("Kythera integration tests", () => {
  test(
    "Arithmetic",
    buildAndTest("num", (out) => {
      expect(out.sum.value).toBe(15);
      expect(out.difference.value).toBe(5);
      expect(out.product.value).toBe(50);
      expect(out.quotient.value).toBe(2);
      expect(out.remainder.value).toBe(0);
    })
  );

  test(
    "AST nodes",
    buildAndTest("nodes", (out) => {
      expect(out.letNode.value).toBe(1);
    })
  );

  test(
    "Literals",
    buildAndTest("literals", (out) => {
      expect(out.intNumLiteral.value).toBe(2)
      expect(out.floatNumLiteral.value).toBe(2.01);
    })
  )

  /*

  test(
    "Functions",
    buildAndTest("fn", (out) => {
      // currying
      // proper scoping
    })
  );

  test(
    "Syntax",
    buildAndTest("syntax", (out) => {
      // function block vs scope block vs struct literal vs struct type literal
    })
  )

  // test typeof on an instance of every type
  test(
    "Typeof",
  )
  */
});
